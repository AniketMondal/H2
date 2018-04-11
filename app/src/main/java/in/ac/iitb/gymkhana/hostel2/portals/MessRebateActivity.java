package in.ac.iitb.gymkhana.hostel2.portals;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import in.ac.iitb.gymkhana.hostel2.R;
import in.ac.iitb.gymkhana.hostel2.ssologin.SessionManager;

public class MessRebateActivity extends AppCompatActivity {

    private SessionManager session;
    private ArrayList<MessRebate> list;
    private TextView status;
    private MessRebateAdapter adapter;
    private ListView listView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portal_lan_mess_activity);
        session = new SessionManager(getApplicationContext());
        context = this;
        if (!session.isLoggedIn()){
            Toast.makeText(getApplicationContext(), "You must be logged in to access the portals", Toast.LENGTH_LONG).show();
            onBackPressed();
        }

        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.portal_add_button);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder addRebateDialogBuilder = new AlertDialog.Builder(context);
                final View addRebateView = getLayoutInflater().inflate(R.layout.portal_mess_add, null);
                addRebateDialogBuilder.setView(addRebateView);

                final Dialog addComplaintDialog = addRebateDialogBuilder.create();
                addComplaintDialog.show();

                final EditText startEdit = addRebateView.findViewById(R.id.mess_add_start);
                final EditText endEdit = addRebateView.findViewById(R.id.mess_add_end);

                final Calendar start = Calendar.getInstance();
                final Calendar end = Calendar.getInstance();
                final DatePickerDialog.OnDateSetListener startSetListner = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        start.set(Calendar.YEAR, year);
                        start.set(Calendar.MONTH, monthOfYear);
                        start.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        startEdit.setText(sdf.format(start.getTime()));
                    }
                };
                final DatePickerDialog.OnDateSetListener endSetListner = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        end.set(Calendar.YEAR, year);
                        end.set(Calendar.MONTH, monthOfYear);
                        end.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        endEdit.setText(sdf.format(end.getTime()));
                    }
                };

                startEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(MessRebateActivity.this, startSetListner, start
                                .get(Calendar.YEAR), start.get(Calendar.MONTH),
                                start.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });
                endEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(MessRebateActivity.this, endSetListner, end
                                .get(Calendar.YEAR), end.get(Calendar.MONTH),
                                end.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                Button cancel = (Button) addRebateView.findViewById(R.id.mess_add_cancel);
                Button submit = (Button) addRebateView.findViewById(R.id.mess_add_submit);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addComplaintDialog.dismiss();
                    }
                });

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        String start = startEdit.getText().toString();
                        String end = endEdit.getText().toString();

                        if (!start.equals("")) {
                            if (!end.equals("")) {
                                new AddLANComplaint().execute(start, end);
                                addComplaintDialog.dismiss();
                                Toast.makeText(context.getApplicationContext(), "Submitting", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context.getApplicationContext(), "Please enter End Date", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context.getApplicationContext(), "Please enter Start Date", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        status = (TextView) findViewById(R.id.portal_status);
        status.setText("Loading");

        list = new ArrayList<MessRebate>();

        adapter = new MessRebateAdapter(this, list);
        listView = (ListView) findViewById(R.id.portal_list);

        new GetLANComplaints().execute();

    }

    void restart() {
        startActivity(getIntent());
        finish();
    }


    class MessRebate {
        String timestamp;
        String start;
        String end;
        String status;

        public MessRebate(String timestamp, String start, String end, String status) {
            this.timestamp = timestamp;
            this.start = start;
            this.end = end;
            this.status = status;
        }

        public String getTimestamp(){
            return timestamp;
        }
        public String getStart() {
            return start;
        }
        public String getEnd() {
            return end;
        }
        public String getStatus() {
            return status;
        }
    }

    class MessRebateAdapter extends ArrayAdapter<MessRebate> {

        public MessRebateAdapter(Context context, ArrayList<MessRebate> items) {
            super(context, 0, items);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View listItemView = convertView;

            if(listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.portal_item, parent, false);
            }

            MessRebate currentItem = getItem(position);

            ImageView icon = (ImageView) listItemView.findViewById(R.id.portal_item_icon);
            TextView timestamp = (TextView) listItemView.findViewById(R.id.portal_item_timestamp);
            TextView start = (TextView) listItemView.findViewById(R.id.portal_item_text1);
            TextView status = (TextView) listItemView.findViewById(R.id.portal_item_status);
            TextView end = (TextView) listItemView.findViewById(R.id.portal_item_text2);

            icon.setImageDrawable(getResources().getDrawable(R.drawable.mess));
            timestamp.setText("Timestamp : " + currentItem.getTimestamp());
            start.setText("Start Date : " + currentItem.getStart());
            status.setText(currentItem.getStatus());
            end.setText("End Date : " + currentItem.getEnd());

            return listItemView;
        }
    }

    class GetLANComplaints extends AsyncTask<Void,Void,String> {

        String url;
        URL URL;

        @Override
        protected void onPreExecute() {
            url = "https://gymkhana.iitb.ac.in/~hostel2/portals/messrebate/app/find.php";
            try {
                URL = new URL(url);
            } catch (Exception e) { }
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpsURLConnection conn;
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
                String urlParameters = "rollno="+session.getRollNo();
                conn.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.close();

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.equals("Error")) {
                        return "Error";
                    } else if (inputLine.equals("Empty")) {
                        return "Empty";
                    }
                    String[] request = inputLine.split("~~");
                    list.add(new MessRebate(request[0], request[1], request[2], request[3]));
                }
                in.close();
                conn.disconnect();
            } catch (Exception e) { }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            if (!list.isEmpty()) {
                status.setVisibility(View.GONE);
                listView.setAdapter(adapter);
            } else if (s.equals("Empty")) {
                listView.setVisibility(View.GONE);
                status.setText("No previous requests for mess rebate");
            } else {
                listView.setVisibility(View.GONE);
                status.setText("Some error ocurred in getting previous requests");
            }
        }
    }

    class AddLANComplaint extends AsyncTask<String,Void,String> {

        String url;
        URL URL;

        @Override
        protected void onPreExecute() {
            url = "https://gymkhana.iitb.ac.in/~hostel2/portals/messrebate/app/add.php";
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
                String urlParameters = "rollno="+session.getRollNo()+"&name="+session.getName()+"&start="+params[0]+"&end="+params[1];
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
            Toast.makeText(context.getApplicationContext(), ""+s, Toast.LENGTH_LONG).show();
            restart();
        }
    }
}