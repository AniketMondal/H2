package in.ac.iitb.gymkhana.hostel2.portals;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import in.ac.iitb.gymkhana.hostel2.R;
import in.ac.iitb.gymkhana.hostel2.ssologin.SessionManager;

public class KFCPortalActivity extends AppCompatActivity {

    WebView kfcWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portal_kfc_activity);

        kfcWebView = findViewById(R.id.kfc_webview);
        kfcWebView.setWebViewClient(new WebViewClient());
        kfcWebView.getSettings().setJavaScriptEnabled(true);
        kfcWebView.getSettings().setLoadWithOverviewMode(true);
        kfcWebView.getSettings().setUseWideViewPort(true);
        kfcWebView.getSettings().setBuiltInZoomControls(true);
        kfcWebView.loadUrl("https://gymkhana.iitb.ac.in/~hostel2/portals/kfc/");

    }

}
