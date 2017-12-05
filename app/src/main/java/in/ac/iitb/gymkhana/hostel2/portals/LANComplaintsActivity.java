package in.ac.iitb.gymkhana.hostel2.portals;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import in.ac.iitb.gymkhana.hostel2.R;
import in.ac.iitb.gymkhana.hostel2.ssologin.SessionManager;

public class LANComplaintsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portal_lan_activity);
        SessionManager session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()){
            Toast.makeText(getApplicationContext(), "You must be logged in to access the portals", Toast.LENGTH_LONG).show();
            onBackPressed();
        }
    }
}
