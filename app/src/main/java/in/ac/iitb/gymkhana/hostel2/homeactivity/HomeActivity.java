package in.ac.iitb.gymkhana.hostel2.homeactivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.BoolRes;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import in.ac.iitb.gymkhana.hostel2.R;
import in.ac.iitb.gymkhana.hostel2.councilactivity.CouncilActivity;
import in.ac.iitb.gymkhana.hostel2.infoactivity.InfoActivity;
import in.ac.iitb.gymkhana.hostel2.settingsactivity.SettingsActivity;
import in.ac.iitb.gymkhana.hostel2.ssologin.LoginPostRequest;
import in.ac.iitb.gymkhana.hostel2.ssologin.LogoutPostRequest;

import static in.ac.iitb.gymkhana.hostel2.WelcomeActivity.cache;
import static in.ac.iitb.gymkhana.hostel2.WelcomeActivity.getCalendar;
import static in.ac.iitb.gymkhana.hostel2.WelcomeActivity.getMenu;
import static in.ac.iitb.gymkhana.hostel2.WelcomeActivity.getNews;
import static in.ac.iitb.gymkhana.hostel2.ssologin.LoginPostRequest.getQueryMap;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.include_tabbed_content).setVisibility(View.VISIBLE);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        HomeAdapter homeAdapter = new HomeAdapter(getSupportFragmentManager());
        viewPager.setAdapter(homeAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorAccent));

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh) {
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setMessage("Refreshing");
            progress.setIndeterminate(true);
            progress.show();
            getMenu(getApplicationContext());
            getNews();
            getCalendar();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(getIntent());
                    finish();
                }
            },1500);
            return true;
        }
        return super.onOptionsItemSelected(item);
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






    class HomeAdapter extends FragmentPagerAdapter {

        public HomeAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) return new HomeMessFragment();
            else if (position == 1) return new HomeNewsFragment();
            else return new HomeCalendarFragment();
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) return "Mess Menu";
            else if (position == 1) return "News";
            else return "Calendar";
        }
    }




    public static class ExpandableItem {

        private String title;
        private String content;
        private boolean expanded;

        public ExpandableItem(String title, String content, Boolean expanded) {
            this.title = title;
            this.content = content;
            this.expanded = expanded;
        }

        public String getTitle() { return title; }
        public String getContent() { return content; }
        public boolean getExpanded() { return expanded; }
    }

    public static class ExpandableItemAdapter extends ArrayAdapter<ExpandableItem> {

        public ExpandableItemAdapter(Context context, ArrayList<ExpandableItem> items) {
            super(context, 0, items);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View listItemView = convertView;

            if(listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.home_expandable_list_item, parent, false);
            }

            ExpandableItem currentItem = getItem(position);

            TextView title = (TextView) listItemView.findViewById(R.id.expandable_title);
            TextView content = (TextView) listItemView.findViewById(R.id.expandable_content);

            title.setText(currentItem.getTitle());
            content.setText(currentItem.getContent());

            if (currentItem.getExpanded()) {
                ImageView arrow = (ImageView) listItemView.findViewById(R.id.expandable_arrow);
                content.setVisibility(View.VISIBLE);
                arrow.setImageResource(R.drawable.up_arrow);
            }

            return listItemView;
        }
    }
}
