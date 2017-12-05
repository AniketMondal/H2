package in.ac.iitb.gymkhana.hostel2.ssologin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import in.ac.iitb.gymkhana.hostel2.R;
import in.ac.iitb.gymkhana.hostel2.WelcomeActivity;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;


/**
 * Created by bhavesh on 28/09/17.
 */

public class DataAccessGetRequest extends AsyncTask<String,Void,String> {

    String url;
    URL URL;

    NavigationView navigationView;
    TextView userNameTextView;
    TextView ldapIDTextView;
    Activity activity;
    ProgressDialog progress;

    public DataAccessGetRequest(NavigationView navigationView, TextView userNameTextView, TextView ldapIDTextView, Activity activity, ProgressDialog progress){
        this.navigationView = navigationView;
        this.userNameTextView = userNameTextView;
        this.ldapIDTextView = ldapIDTextView;
        this.activity = activity;
        this.progress = progress;
    }

    @Override
    protected void onPreExecute() {
        url = "https://gymkhana.iitb.ac.in/sso/user/api/user/?fields=first_name,last_name,username,roll_number";
        try {
            URL = new URL(url);
        } catch (Exception e) { }
    }

    @Override
    protected String doInBackground(String... ACCESS_TOKEN) {
        HttpsURLConnection conn;
        StringBuffer response = new StringBuffer();

        try {
            // To ignore the certificate error on non-IITB network
            // ------------------START------------------
            TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {return null;}
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    if (hostname.contains("iitb.ac.in"))
                        return true;
                    else
                        return false;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            // ------------------END------------------

            conn = (HttpsURLConnection) URL.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Host", "gymkhana.iitb.ac.in");
            conn.setRequestProperty("Authorization", "Bearer "+ACCESS_TOKEN[0]);

            // Send get request
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            conn.disconnect();
        } catch (Exception e) { }

        return response.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(s);
            Log.e("GET JSON",""+jsonObject.toString());
            String name = jsonObject.getString("first_name") + " " + jsonObject.getString("last_name");
            String ldapid = jsonObject.getString("username");
            String rollno = jsonObject.getString("roll_number");

            new CheckUserPostRequest(navigationView, userNameTextView, ldapIDTextView, activity, progress, name, ldapid, rollno).execute();
        } catch (Exception e) {}
    }

}
