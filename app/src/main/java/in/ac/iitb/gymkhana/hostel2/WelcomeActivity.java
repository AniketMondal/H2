package in.ac.iitb.gymkhana.hostel2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import in.ac.iitb.gymkhana.hostel2.homeactivity.HomeActivity;
import in.ac.iitb.gymkhana.hostel2.messnotification.AlarmBootReceiver;
import in.ac.iitb.gymkhana.hostel2.messnotification.BreakfastAlarmMaker;
import in.ac.iitb.gymkhana.hostel2.messnotification.DinnerAlarmMaker;
import in.ac.iitb.gymkhana.hostel2.messnotification.DinnerAlarmReceiver;
import in.ac.iitb.gymkhana.hostel2.messnotification.LunchAlarmMaker;
import in.ac.iitb.gymkhana.hostel2.messnotification.TiffinAlarmMaker;

public class WelcomeActivity extends AppCompatActivity {

    public static CacheManager cache;

    public static Bitmap calendar;

    public static String menuURL = "https://gymkhana.iitb.ac.in/~hostel2/menu_app.json";
    public static String newsURL = "https://gymkhana.iitb.ac.in/~hostel2/appdata/news_app.json";
    public static String calURL = "https://gymkhana.iitb.ac.in/~hostel2/appdata/calendar.jpg";
    public static String cultGCurl = "https://gymkhana.iitb.ac.in/~cultural/index.php?key1=arch&key2=gc";
    public static String techGCurl = "http://tech-iitb.org/gcrankings-2017-18";
    public static String sportsGCurl = "https://gymkhana.iitb.ac.in/~sports/index.php?r=events/gc";

    public static String SSO_ACCESS_TOKEN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        cache = new CacheManager(getApplicationContext());

        TextView tx = (TextView) findViewById(R.id.hostel_name);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/NaughtySquirrelDemo.ttf");
        tx.setTypeface(custom_font);

        BreakfastAlarmMaker.makeAlarm(getApplicationContext());
        LunchAlarmMaker.makeAlarm(getApplicationContext());
        TiffinAlarmMaker.makeAlarm(getApplicationContext());
        DinnerAlarmMaker.makeAlarm(getApplicationContext());
        ComponentName receiver = new ComponentName(getApplicationContext(), AlarmBootReceiver.class);
        PackageManager pm = getApplicationContext().getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        getMenu(this);
        getNews();
        getCalendar();

        Log.e("TOKEN", "" + FirebaseInstanceId.getInstance().getToken());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent().setClass(WelcomeActivity.this, HomeActivity.class));
                finish();
            }
        },1500);

    }


    static class GetMenu extends AsyncTask<Void,Void,String> {

        String url;
        Context context;

        public GetMenu(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            url = menuURL;
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


                URL url1 = new URL(url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url1.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder=new StringBuilder();
                String reader;
                while ((reader = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(reader+"\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();
            } catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null) {
                Toast.makeText(context, "Please check your internet conection\nLoading cached data", Toast.LENGTH_LONG).show();
            } else {
                cache.addMenu(s);
                String[] now = Calendar.getInstance().getTime().toString().split(" ");
                cache.addTime(now[1] + " " + now[2] + " " + now[3]);
            }
        }
    }

    public static void getMenu(Context context) {
        new GetMenu(context).execute();
    }

    static class GetNews extends AsyncTask<Void,Void,String> {

        String url;

        @Override
        protected void onPreExecute() {
            url = newsURL;
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


                URL url1 = new URL(url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url1.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder=new StringBuilder();
                String reader;
                while ((reader = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(reader+"\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();
            } catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null) {
            } else {
                cache.addNews(s);
            }
        }
    }

    public static void getNews() {
        new GetNews().execute();
    }

    static class GetCalendar extends AsyncTask<Void,Void,Bitmap> {

        String url;

        @Override
        protected void onPreExecute() {
            url = calURL;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
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


                URL url1 = new URL(url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url1.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                InputStream input = httpURLConnection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap s) {
            calendar = s;
        }
    }

    public static void getCalendar() {
        new GetCalendar().execute();
    }

}
