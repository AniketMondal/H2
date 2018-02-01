package in.ac.iitb.gymkhana.hostel2;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import in.ac.iitb.gymkhana.hostel2.councilactivity.CouncilActivity;
import in.ac.iitb.gymkhana.hostel2.homeactivity.HomeActivity;
import in.ac.iitb.gymkhana.hostel2.infoactivity.InfoActivity;
import in.ac.iitb.gymkhana.hostel2.notificationsactivity.NotificationsActivity;
import in.ac.iitb.gymkhana.hostel2.portals.PortalsActivity;
import in.ac.iitb.gymkhana.hostel2.settingsactivity.SettingsActivity;
import in.ac.iitb.gymkhana.hostel2.ssologin.LoginPostRequest;
import in.ac.iitb.gymkhana.hostel2.ssologin.SessionManager;

import static in.ac.iitb.gymkhana.hostel2.ssologin.LoginPostRequest.getQueryMap;

/**
 * Created by bhavesh on 25/11/17.
 */

public class CommonFunctions {

    public static boolean navigationItemSelect(MenuItem item, final Activity activity) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                activity.startActivity(new Intent().setClass(activity, HomeActivity.class));
                activity.finish();
                break;
            case R.id.nav_notifications:
                activity.startActivity(new Intent().setClass(activity, NotificationsActivity.class));
                activity.finish();
                break;
            case R.id.nav_council:
                activity.startActivity(new Intent().setClass(activity, CouncilActivity.class));
                activity.finish();
                break;
            case R.id.nav_settings:
                activity.startActivity(new Intent().setClass(activity, SettingsActivity.class));
                activity.finish();
                break;
            case R.id.nav_info:
                activity.startActivity(new Intent().setClass(activity, InfoActivity.class));
                activity.finish();
                break;
            case R.id.nav_portals:
                activity.startActivity(new Intent().setClass(activity, PortalsActivity.class));
                activity.finish();
                break;
            case R.id.nav_login: {
                final ProgressDialog progress = new ProgressDialog(activity);
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setMessage("Logging in");
                progress.setIndeterminate(true);
                login(activity, progress);
                break;
            }
            case R.id.nav_logout: {
                logout(activity);
                Toast.makeText(activity.getApplicationContext(),"Logout Successful",Toast.LENGTH_SHORT).show();
                break;
            }
        }

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }





    private static void login(final Activity activity, final ProgressDialog progress){
        final NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        final TextView userNameTextView = (TextView) activity.findViewById(R.id.username);
        final TextView ldapIDTextView = (TextView) activity.findViewById(R.id.ldapid);
        AlertDialog.Builder loginDialogBuilder = new AlertDialog.Builder(activity);
        View loginView = activity.getLayoutInflater().inflate(R.layout.login_layout, null);
        loginDialogBuilder.setView(loginView);
        EditText loginEdittext = (EditText)  loginView.findViewById(R.id.login_edittext);
        loginEdittext.requestFocus();
        final Dialog loginDialog = loginDialogBuilder.create();
        loginDialog.show();
        loginView.requestFocus();
        WebView loginWebView = (WebView) loginView.findViewById(R.id.login_webview);
        loginWebView.loadUrl("https://gymkhana.iitb.ac.in/sso/oauth/authorize/?client_id=XWdEl57bq3NkT1XJac4uDKXOlURJl0yIreldL8U3&response_type=code&scope=ldap profile program&redirect_uri=http://www.google.co.in/&state=some_state");
        loginWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                if(url.contains("http://www.google.co.in/")){
                    if(url.contains("error=access_denied")){
                        Toast.makeText(activity.getApplicationContext(),"Authorization Unsuccessful",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String AUTHORIZATION_CODE = getQueryMap(url);
                        Log.e("AUTH",""+AUTHORIZATION_CODE);
                        if (AUTHORIZATION_CODE == null) {
                            login(activity, progress);
                        } else {
                            new LoginPostRequest(navigationView, userNameTextView,ldapIDTextView, activity, progress).execute(AUTHORIZATION_CODE);
                        }
                    }
                    loginDialog.dismiss();
                    progress.show();
                }
                return true;
            }
            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Using non-IITB network can be insecure. Proceed?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.proceed();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.cancel();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }





    private static void logout(Activity activity){
        SessionManager session = new SessionManager(activity.getApplicationContext());
        session.logoutUser();

        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_logout).setVisible(false);
        nav_Menu.findItem(R.id.nav_login).setVisible(true);

        TextView userNameTextView = (TextView) activity.findViewById(R.id.username);
        TextView ldapIDTextView = (TextView) activity.findViewById(R.id.ldapid);
        userNameTextView.setText("User Name");
        ldapIDTextView.setText("LDAP ID");

        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(null);
        } else {
            cookieManager.removeAllCookie();
        }
    }



    public static void setUser(Activity activity){
        SessionManager session = new SessionManager(activity.getApplicationContext());
        if (session.isLoggedIn()){
            NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            nav_Menu.findItem(R.id.nav_logout).setVisible(true);

            LinearLayout drawerHeader = (LinearLayout) navigationView.getHeaderView(0);
            TextView userNameTextView = (TextView) drawerHeader.findViewById(R.id.username);
            TextView ldapIDTextView = (TextView) drawerHeader.findViewById(R.id.ldapid);
            userNameTextView.setText(session.getName());
            ldapIDTextView.setText(session.getLDAPid());
        }
    }

}
