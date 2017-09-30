package in.ac.iitb.gymkhana.hostel2.ssologin;

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

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;

import in.ac.iitb.gymkhana.hostel2.R;

import static in.ac.iitb.gymkhana.hostel2.WelcomeActivity.SSO_ACCESS_TOKEN;

/**
 * Created by bhavesh on 28/09/17.
 */

public class LogoutPostRequest extends AsyncTask<String,Void,String> {


    String url;
    URL URL;

    NavigationView navigationView;
    TextView userNameTextView;
    TextView ldapIDTextView;
    Context context;

    public LogoutPostRequest(NavigationView navigationView, TextView userNameTextView, TextView ldapIDTextView, Context context){
        this.navigationView = navigationView;
        this.userNameTextView = userNameTextView;
        this.ldapIDTextView = ldapIDTextView;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        url = "https://gymkhana.iitb.ac.in/sso/oauth/revoke_token/";
        try {
            URL = new URL(url);
        } catch (Exception e) { }
    }

    @Override
    protected String doInBackground(String... key) {
        HttpsURLConnection conn;
        StringBuffer response = new StringBuffer();

        try {
            conn = (HttpsURLConnection) URL.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Host", "gymkhana.iitb.ac.in");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");


            String urlParameters = "token="+SSO_ACCESS_TOKEN+"&client_id=XWdEl57bq3NkT1XJac4uDKXOlURJl0yIreldL8U3&client_secret=ydDAeGkntYpK2Nb46K2KuQ1xMnr6c0XEuTf5gS8TD2AuAFhx7JQSamVJV9mT2Pm4JspgRjOjELKEXOwZX54kL5IpX02Wgskyqe2FZi7KeOR5kHareA12ZeU6N3NfZjJm&token_type_hint=access_token";

            // Send post request
            conn.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_logout).setVisible(false);
        nav_Menu.findItem(R.id.nav_login).setVisible(true);
        userNameTextView.setText("User Name");
        ldapIDTextView.setText("LDAP ID");
        Toast.makeText(context,"Logout Successful",Toast.LENGTH_SHORT).show();
    }

}
