package in.ac.iitb.gymkhana.hostel2.councilactivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import in.ac.iitb.gymkhana.hostel2.R;
import in.ac.iitb.gymkhana.hostel2.homeactivity.HomeActivity;
import in.ac.iitb.gymkhana.hostel2.infoactivity.InfoActivity;
import in.ac.iitb.gymkhana.hostel2.settingsactivity.SettingsActivity;
import in.ac.iitb.gymkhana.hostel2.ssologin.LoginPostRequest;
import in.ac.iitb.gymkhana.hostel2.ssologin.LogoutPostRequest;

import static in.ac.iitb.gymkhana.hostel2.ssologin.LoginPostRequest.getQueryMap;

public class CouncilActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.include_tabbed_content).setVisibility(View.VISIBLE);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        CouncilAdapter councilAdapter = new CouncilAdapter(getSupportFragmentManager());
        viewPager.setAdapter(councilAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private static long back_pressed;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (back_pressed + 2000 > System.currentTimeMillis()){
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                TextView userNameTextView = (TextView) findViewById(R.id.username);
                TextView ldapIDTextView = (TextView) findViewById(R.id.ldapid);
                new LogoutPostRequest(navigationView, userNameTextView, ldapIDTextView, getApplicationContext()).execute();

                CookieManager cookieManager = CookieManager.getInstance();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cookieManager.removeAllCookies(null);
                } else {
                    cookieManager.removeAllCookie();
                }
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
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                startActivity(new Intent().setClass(this, HomeActivity.class));
                finish();
                break;
            case R.id.nav_council:
                startActivity(new Intent().setClass(this, CouncilActivity.class));
                finish();
                break;
            case R.id.nav_settings:
                startActivity(new Intent().setClass(this, SettingsActivity.class));
                finish();
                break;
            case R.id.nav_info:
                startActivity(new Intent().setClass(this, InfoActivity.class));
                finish();
                break;
            case R.id.nav_login: {
                final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                final TextView userNameTextView = (TextView) findViewById(R.id.username);
                final TextView ldapIDTextView = (TextView) findViewById(R.id.ldapid);
                final Context context = this;
                AlertDialog.Builder loginDialogBuilder = new AlertDialog.Builder(context);
                View loginView = getLayoutInflater().inflate(R.layout.login_layout, null);
                loginDialogBuilder.setView(loginView);
                EditText loginEdittext = (EditText)  loginView.findViewById(R.id.login_edittext);
                loginEdittext.requestFocus();
                final Dialog loginDialog = loginDialogBuilder.create();
                loginDialog.show();
                loginView.requestFocus();
                WebView loginWebView = (WebView) loginView.findViewById(R.id.login_webview);
                loginWebView.loadUrl("https://gymkhana.iitb.ac.in/sso/oauth/authorize/?client_id=XWdEl57bq3NkT1XJac4uDKXOlURJl0yIreldL8U3&response_type=code&scope=ldap profile&redirect_uri=http://www.google.co.in/&state=some_state");
                loginWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        if(url.contains("http://www.google.co.in/")){
                            if(url.contains("error=access_denied")){
                                Toast.makeText(getApplicationContext(),"Authorization Unsuccessful",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String AUTHORIZATION_CODE = getQueryMap(url);
                                Log.e("AUTH",""+AUTHORIZATION_CODE);
                                if (AUTHORIZATION_CODE == null) {
                                    loginDialog.dismiss();
                                    AlertDialog.Builder loginDialogBuilder = new AlertDialog.Builder(context);
                                    View loginView = getLayoutInflater().inflate(R.layout.login_layout, null);
                                    loginDialogBuilder.setView(loginView);
                                    EditText loginEdittext = (EditText)  loginView.findViewById(R.id.login_edittext);
                                    loginEdittext.requestFocus();
                                    final Dialog loginDialog2 = loginDialogBuilder.create();
                                    loginDialog2.show();
                                    loginView.requestFocus();
                                    WebView loginWebView = (WebView) loginView.findViewById(R.id.login_webview);
                                    loginWebView.loadUrl("https://gymkhana.iitb.ac.in/sso/oauth/authorize/?client_id=XWdEl57bq3NkT1XJac4uDKXOlURJl0yIreldL8U3&response_type=code&scope=ldap profile&redirect_uri=http://www.google.co.in/&state=some_state");
                                    loginWebView.setWebViewClient(new WebViewClient() {
                                        @Override
                                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                            view.loadUrl(url);
                                            if(url.contains("http://www.google.co.in/")){
                                                if(url.contains("error=access_denied")){
                                                    Toast.makeText(getApplicationContext(),"Authorization Unsuccessful",Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    String AUTHORIZATION_CODE = getQueryMap(url);
                                                    Log.e("AUTH",""+AUTHORIZATION_CODE);
                                                    if (AUTHORIZATION_CODE == null) {
                                                        Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_SHORT).show();
                                                    }
                                                    new LoginPostRequest(navigationView, userNameTextView,ldapIDTextView, getApplicationContext()).execute(AUTHORIZATION_CODE);
                                                }
                                                loginDialog2.dismiss();
                                            }
                                            return true;
                                        }
                                        @Override
                                        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                                            handler.proceed(); // Ignore SSL certificate errors
                                        }
                                    });
                                }
                                new LoginPostRequest(navigationView, userNameTextView,ldapIDTextView, getApplicationContext()).execute(AUTHORIZATION_CODE);
                            }
                            loginDialog.dismiss();
                        }
                        return true;
                    }
                    @Override
                    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                        handler.proceed(); // Ignore SSL certificate errors
                    }
                });
                break;
            }
            case R.id.nav_logout: {
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                TextView userNameTextView = (TextView) findViewById(R.id.username);
                TextView ldapIDTextView = (TextView) findViewById(R.id.ldapid);
                new LogoutPostRequest(navigationView, userNameTextView, ldapIDTextView, getApplicationContext()).execute();

                CookieManager cookieManager = CookieManager.getInstance();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cookieManager.removeAllCookies(null);
                } else {
                    cookieManager.removeAllCookie();
                }
                break;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    class CouncilAdapter extends FragmentPagerAdapter {

        public CouncilAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) return new CouncilHeadsFragment();
            else if (position == 1) return new CouncilWebFragment();
            else if (position == 2) return new CouncilMaintFragment();
            else if (position == 3) return new CouncilMessFragment();
            else if (position == 4) return new CouncilCultFragment();
            else if (position == 5) return new CouncilSportsFragment();
            else if (position == 6) return new CouncilTechFragment();
            else return new CouncilAlumniFragment();
        }

        @Override
        public int getCount() {
            return 8;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) return "Heads";
            else if (position == 1) return "Web";
            else if (position == 2) return "Maint";
            else if (position == 3) return "Mess";
            else if (position == 4) return "Cult";
            else if (position == 5) return "Sports";
            else if (position == 6) return "Tech";
            else return "Alumni";
        }
    }



    public static class CouncilMember {

        private int photoID;
        private String name;
        private String post;
        private String room;
        private String phone;

        public CouncilMember(int photoID, String name, String post, String room, String phone){
            this.photoID = photoID;
            this.name = name;
            this.post = post;
            this.room = room;
            this.phone = phone;
        }

        public int getPhotoID() { return photoID; }
        public String getName() { return name; }
        public String getPost() { return post; }
        public String getRoom() { return room; }
        public String getPhone() { return phone; }

    }






    public static class CouncilMemberAdapter extends ArrayAdapter<CouncilMember> {

        public CouncilMemberAdapter(Context context, ArrayList<CouncilMember> members) {
            super(context, 0, members);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View listItemView = convertView;

            if(listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.council_member, parent, false);
            }

            CouncilMember currentMember = getItem(position);

            ImageView photo = (ImageView) listItemView.findViewById(R.id.council_member_photo);
            TextView name = (TextView) listItemView.findViewById(R.id.council_member_name);
            TextView post = (TextView) listItemView.findViewById(R.id.council_member_post);
            TextView room = (TextView) listItemView.findViewById(R.id.council_member_room);
            TextView phone = (TextView) listItemView.findViewById(R.id.council_member_phone);

            photo.setImageResource(currentMember.getPhotoID());
            name.setText(currentMember.getName());
            post.setText(currentMember.getPost());
            room.setText(currentMember.getRoom());
            phone.setText(currentMember.getPhone());

            return listItemView;
        }

    }
}
