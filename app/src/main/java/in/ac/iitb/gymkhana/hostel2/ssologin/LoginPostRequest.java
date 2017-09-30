package in.ac.iitb.gymkhana.hostel2.ssologin;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static in.ac.iitb.gymkhana.hostel2.WelcomeActivity.SSO_ACCESS_TOKEN;


/**
 * Created by bhavesh on 28/09/17.
 */

public class LoginPostRequest extends AsyncTask<String,Void,String>  {

    String url;
    URL URL;

    NavigationView navigationView;
    TextView userNameTextView;
    TextView ldapIDTextView;
    Context context;

    public LoginPostRequest(NavigationView navigationView, TextView userNameTextView, TextView ldapIDTextView, Context context){
        this.navigationView = navigationView;
        this.userNameTextView = userNameTextView;
        this.ldapIDTextView = ldapIDTextView;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        url = "https://gymkhana.iitb.ac.in/sso/oauth/token/";
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
            conn.setRequestProperty("Authorization", "Basic WFdkRWw1N2JxM05rVDFYSmFjNHVES1hPbFVSSmwweUlyZWxkTDhVMzp5ZERBZUdrbnRZcEsyTmI0NksyS3VRMXhNbnI2YzBYRXVUZjVnUzhURDJBdUFGaHg3SlFTYW1WSlY5bVQyUG00SnNwZ1JqT2pFTEtFWE93Wlg1NGtMNUlwWDAyV2dza3lxZTJGWmk3S2VPUjVrSGFyZUExMlplVTZOM05mWmpKbQ==");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");


            String urlParameters = "code=" + key[0] + "&redirect_uri=http://www.google.co.in/&grant_type=authorization_code";

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
        } catch (Exception e) { }

        return response.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(s);
            SSO_ACCESS_TOKEN = jsonObject.getString("access_token");
            Log.e("ACCESS",""+SSO_ACCESS_TOKEN);
            new DataAccessGetRequest(navigationView, userNameTextView,ldapIDTextView,context).execute(SSO_ACCESS_TOKEN);
        } catch (Exception e) {}
    }


    public static String getQueryMap(String query)
    {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map.get("code");
    }
}
