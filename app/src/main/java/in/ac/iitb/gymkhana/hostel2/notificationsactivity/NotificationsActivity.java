package in.ac.iitb.gymkhana.hostel2.notificationsactivity;

import android.graphics.Typeface;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import in.ac.iitb.gymkhana.hostel2.CacheManager;
import in.ac.iitb.gymkhana.hostel2.CommonFunctions;
import in.ac.iitb.gymkhana.hostel2.R;
import in.ac.iitb.gymkhana.hostel2.homeactivity.HomeActivity;


public class NotificationsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.include_notifications).setVisibility(View.VISIBLE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        CommonFunctions.setUser(this);

        final ArrayList<HomeActivity.ExpandableItem> notificationList = new ArrayList<HomeActivity.ExpandableItem>();

        CacheManager cache = new CacheManager(getApplicationContext());
        String[] notifications = cache.getNotifications().split("~~~~");
        for (int i = 1; i < notifications.length; i++) {
            String[] notification = notifications[i].split("~~");
            notificationList.add(new HomeActivity.ExpandableItem(notification[0], notification[1], true));
        }

        Collections.reverse(notificationList);

        HomeActivity.ExpandableItemAdapter adapter = new HomeActivity.ExpandableItemAdapter(this, notificationList);
        ListView listView = (ListView) findViewById(R.id.notification_list);
        listView.setAdapter(adapter);

        if (notificationList.isEmpty()) {
            TextView no = new TextView(this);
            no.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
            no.setPadding(8, 8, 8, 8);
            no.setGravity(Gravity.CENTER_HORIZONTAL);
            no.setTextSize(15);
            no.setTypeface(no.getTypeface(), Typeface.ITALIC);
            no.setText("No recent notifications");
            listView.addFooterView(no);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView content = (TextView) view.findViewById(R.id.expandable_content);
                ImageView arrow = (ImageView) view.findViewById(R.id.expandable_arrow);
                if(content.isShown()){
                    content.setVisibility(View.GONE);
                    content.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up));
                    arrow.setImageResource(R.drawable.down_arrow);
                }
                else{
                    content.setVisibility(View.VISIBLE);
                    content.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down));
                    arrow.setImageResource(R.drawable.up_arrow);
                }
            }
        });
    }

    private static long back_pressed;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (back_pressed + 2000 > System.currentTimeMillis()){
                super.onBackPressed();
            }
            else{
                Toast.makeText(getBaseContext(), "Press twice to exit", Toast.LENGTH_SHORT).show();
                back_pressed = System.currentTimeMillis();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return CommonFunctions.navigationItemSelect(item, this);
    }


}
