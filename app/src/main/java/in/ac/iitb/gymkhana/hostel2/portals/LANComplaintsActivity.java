package in.ac.iitb.gymkhana.hostel2.portals;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import in.ac.iitb.gymkhana.hostel2.R;
import in.ac.iitb.gymkhana.hostel2.ssologin.SessionManager;

import static in.ac.iitb.gymkhana.hostel2.WelcomeActivity.lanComplaintsURL;

public class LANComplaintsActivity extends AppCompatActivity {

    private SessionManager session;
    private ArrayList<LanComplaint> list;
    private TextView status;
    private LanComplaintAdapter adapter;
    private ListView listView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portal_lan_activity);
        session = new SessionManager(getApplicationContext());
        context = this;
        if (!session.isLoggedIn()){
            Toast.makeText(getApplicationContext(), "You must be logged in to access the portals", Toast.LENGTH_LONG).show();
            onBackPressed();
        }

        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.add_lan_complaint);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder addComplaintDialogBuilder = new AlertDialog.Builder(context);
                final View addComplaintView = getLayoutInflater().inflate(R.layout.portal_lan_add, null);
                addComplaintDialogBuilder.setView(addComplaintView);

                final Dialog addComplaintDialog = addComplaintDialogBuilder.create();
                addComplaintDialog.show();

                Button cancel = (Button) addComplaintView.findViewById(R.id.lan_add_cancel);
                Button submit = (Button) addComplaintView.findViewById(R.id.lan_add_submit);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addComplaintDialog.dismiss();
                    }
                });

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText roomEdit = addComplaintView.findViewById(R.id.lan_add_room);
                        EditText problemEdit = addComplaintView.findViewById(R.id.lan_add_problem);

                        String room = roomEdit.getText().toString();
                        String problem = problemEdit.getText().toString();

                        if (!room.equals("")) {
                            if (!problem.equals("")) {
                                new AddLANComplaint().execute(room, problem);
                                addComplaintDialog.dismiss();
                                Toast.makeText(context.getApplicationContext(), "Submitting", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context.getApplicationContext(), "Please enter Problem", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context.getApplicationContext(), "Please enter Room Number", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        status = (TextView) findViewById(R.id.lan_status);
        status.setText("Loading");

        list = new ArrayList<LanComplaint>();

        adapter = new LanComplaintAdapter(this, list);
        listView = (ListView) findViewById(R.id.lan_complaint_list);

        new GetLANComplaints().execute();

    }

    void restart() {
        startActivity(getIntent());
        finish();
    }


    class LanComplaint {
        String date;
        String room;
        String status;

        public LanComplaint(String date, String room, String status) {
            this.date = date;
            this.room = room;
            this.status = status;
        }

        public String getDate(){
            return date;
        }
        public String getRoom() {
            return room;
        }
        public String getStatus() {
            return status;
        }
    }

    class LanComplaintAdapter extends ArrayAdapter<LanComplaint> {

        public LanComplaintAdapter(Context context, ArrayList<LanComplaint> items) {
            super(context, 0, items);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View listItemView = convertView;

            if(listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.portal_lan_complaint_item, parent, false);
            }

            LanComplaint currentItem = getItem(position);

            TextView date = (TextView) listItemView.findViewById(R.id.lan_complaint_date);
            TextView room = (TextView) listItemView.findViewById(R.id.lan_complaint_room);
            TextView status = (TextView) listItemView.findViewById(R.id.lan_complaint_status);

            date.setText("Date : " + currentItem.getDate());
            room.setText("Room : " + currentItem.getRoom());
            status.setText(currentItem.getStatus());

            return listItemView;
        }
    }

    class GetLANComplaints extends AsyncTask<Void,Void,String> {

        String url;

        @Override
        protected void onPreExecute() {
            url = lanComplaintsURL;
        }

        @Override
        protected String doInBackground(Void... voids) {
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


                URL url1 = new URL(lanComplaintsURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url1.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String reader;
                while ((reader = bufferedReader.readLine()) != null) {
                    String[] complaint = reader.split("~~");
                    if (session.getRollNo().equals(complaint[1])) {
                        list.add(new LanComplaint(complaint[0], complaint[3], complaint[5]));
                    }
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return "";
            } catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (!list.isEmpty()) {
                status.setVisibility(View.GONE);
                listView.setAdapter(adapter);
            } else {
                listView.setVisibility(View.GONE);
                status.setText("No previous LAN Complaints registered");
            }
        }
    }

    class AddLANComplaint extends AsyncTask<String,Void,String> {

        String url;
        URL URL;

        @Override
        protected void onPreExecute() {
            url = "https://gymkhana.iitb.ac.in/~hostel2/lancomplaints/addLANcomplaint.php";
            try {
                URL = new URL(url);
            } catch (Exception e) { }
        }

        @Override
        protected String doInBackground(String... params) {
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
                String urlParameters = "rollno="+session.getRollNo()+"&name="+session.getName()+"&room="+params[0]+"&problem="+params[1];
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
            Toast.makeText(context.getApplicationContext(), ""+s, Toast.LENGTH_SHORT).show();
            restart();
        }
    }
}