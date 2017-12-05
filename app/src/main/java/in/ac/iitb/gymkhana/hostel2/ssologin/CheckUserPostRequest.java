package in.ac.iitb.gymkhana.hostel2.ssologin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.webkit.CookieManager;
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

import static in.ac.iitb.gymkhana.hostel2.WelcomeActivity.SSO_ACCESS_TOKEN;


/**
 * Created by bhavesh on 27/11/17.
 */

public class CheckUserPostRequest extends AsyncTask<String,Void,String> {

    String url;
    URL URL;

    NavigationView navigationView;
    TextView userNameTextView;
    TextView ldapIDTextView;
    Activity activity;
    ProgressDialog progress;

    String name;
    String ldapid;
    String rollno;

    public CheckUserPostRequest(NavigationView navigationView, TextView userNameTextView, TextView ldapIDTextView, Activity activity, ProgressDialog progress, String name, String ldapid, String rollno){
        this.navigationView = navigationView;
        this.userNameTextView = userNameTextView;
        this.ldapIDTextView = ldapIDTextView;
        this.activity = activity;
        this.progress = progress;
        this.name = name;
        this.ldapid = ldapid;
        this.rollno = rollno;
    }

    @Override
    protected void onPreExecute() {
        url = "https://gymkhana.iitb.ac.in/~hostel2/appdata/checkuser.php";
        try {
            URL = new URL(url);
        } catch (Exception e) { }
    }

    @Override
    protected String doInBackground(String... key) {
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
            conn.setRequestMethod("POST");
            String urlParameters = "rollno="+rollno;
            conn.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

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
        if (s == null) {
            CookieManager cookieManager = CookieManager.getInstance();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.removeAllCookies(null);
            } else {
                cookieManager.removeAllCookie();
            }
            progress.dismiss();
            Toast.makeText(activity.getApplicationContext(),"Unable to Login",Toast.LENGTH_SHORT).show();
        } else if (s.equals("True")) {
            SessionManager session = new SessionManager(activity.getApplicationContext());
            session.createLoginSession(name, ldapid, rollno);

            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            nav_Menu.findItem(R.id.nav_logout).setVisible(true);
            userNameTextView.setText(name);
            ldapIDTextView.setText(ldapid);
            progress.dismiss();
            Toast.makeText(activity.getApplicationContext(),"Login Successful",Toast.LENGTH_SHORT).show();
        } else {
            progress.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(s);
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            CookieManager cookieManager = CookieManager.getInstance();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.removeAllCookies(null);
            } else {
                cookieManager.removeAllCookie();
            }
        }
    }
}
