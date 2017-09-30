package in.ac.iitb.gymkhana.hostel2.infoactivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import in.ac.iitb.gymkhana.hostel2.R;
import in.ac.iitb.gymkhana.hostel2.councilactivity.CouncilActivity;
import in.ac.iitb.gymkhana.hostel2.homeactivity.HomeActivity;
import in.ac.iitb.gymkhana.hostel2.settingsactivity.SettingsActivity;
import in.ac.iitb.gymkhana.hostel2.ssologin.LoginPostRequest;
import in.ac.iitb.gymkhana.hostel2.ssologin.LogoutPostRequest;

import static in.ac.iitb.gymkhana.hostel2.ssologin.LoginPostRequest.getQueryMap;

public class InfoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.include_tabbed_content).setVisibility(View.VISIBLE);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        InfoAdapter infoAdapter = new InfoAdapter(getSupportFragmentManager());
        viewPager.setAdapter(infoAdapter);

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



    class InfoAdapter extends FragmentPagerAdapter {

        public InfoAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) return new InfoCultFragment();
            else if (position == 1) return new InfoSportsFragment();
            else return new InfoTechFragment();
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) return "Cult";
            else if (position == 1) return "Sports";
            else return "Tech";
        }
    }


    public static class CarouselAdapter extends PagerAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        private Integer [] images;

        public CarouselAdapter(Context context, Integer[] images) {
            this.context = context;
            this.images = images;
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(View container, int position) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View carouselView = layoutInflater.inflate(R.layout.info_carousel_layout, null);
            ImageView imageView = (ImageView) carouselView.findViewById(R.id.carousel_imageview);
            imageView.setImageResource(images[position]);

            ViewPager viewPager = (ViewPager) container;
            viewPager.addView(carouselView, 0);
            return carouselView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ViewPager viewPager = (ViewPager) container;
            View carouselView = (View) object;
            viewPager.removeView(carouselView);
        }
    }
}
