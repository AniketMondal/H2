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
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

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
    Context context;

    public DataAccessGetRequest(NavigationView navigationView, TextView userNameTextView, TextView ldapIDTextView, Context context){
        this.navigationView = navigationView;
        this.userNameTextView = userNameTextView;
        this.ldapIDTextView = ldapIDTextView;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        url = "https://gymkhana.iitb.ac.in/sso/user/api/user/?fields=first_name,last_name,username";
        try {
            URL = new URL(url);
        } catch (Exception e) { }
    }

    @Override
    protected String doInBackground(String... access) {
        HttpsURLConnection conn;
        StringBuffer response = new StringBuffer();

        try {
            conn = (HttpsURLConnection) URL.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Host", "gymkhana.iitb.ac.in");
            conn.setRequestProperty("Authorization", "Bearer "+access[0]);

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
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            nav_Menu.findItem(R.id.nav_logout).setVisible(true);
            userNameTextView.setText(jsonObject.getString("first_name") + " " + jsonObject.getString("last_name"));
            ldapIDTextView.setText(jsonObject.getString("username"));
            Toast.makeText(context,"Login Successful",Toast.LENGTH_SHORT).show();

        } catch (Exception e) {}
    }

}
