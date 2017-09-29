package in.ac.iitb.gymkhana.hostel2.infoactivity;

import android.app.ProgressDialog;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.ac.iitb.gymkhana.hostel2.R;
import in.ac.iitb.gymkhana.hostel2.WelcomeActivity;

import static in.ac.iitb.gymkhana.hostel2.infoactivity.InfoActivity.CarouselAdapter;

/**
 * Created by bhavesh on 21/09/17.
 */

public class InfoTechFragment extends Fragment {

    ViewPager carouselViewPager;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;
    private ProgressDialog progress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_fragment_content, container, false);

        TextView infoText = (TextView) view.findViewById(R.id.info_text);
        infoText.setText("Hostel 2 is well equipped for the tech enthusiasts to design and implement their ideas. The tech room is getting better everyday with new improvements and lots of workshops and activities for the hostel inmates are coming soon.");

        carouselViewPager = (ViewPager) view.findViewById(R.id.carousel_viewpager);
        sliderDotspanel = (LinearLayout) view.findViewById(R.id.carousel_dots);

        CarouselAdapter carouselAdapter = new CarouselAdapter(getContext(),  new Integer[]{R.mipmap.abc1, R.mipmap.abc2, R.mipmap.abc3, R.mipmap.abc4, R.mipmap.abc5});
        carouselViewPager.setAdapter(carouselAdapter);

        dotscount = carouselAdapter.getCount();
        dots = new ImageView[dotscount];

        for(int i = 0; i < dotscount; i++){
            dots[i] = new ImageView(getContext());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.nonactive_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            sliderDotspanel.addView(dots[i], params);
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.active_dot));

        carouselViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                for(int i = 0; i< dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.nonactive_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        final WebView gcWebView = (WebView) view.findViewById(R.id.gc_webview);
        Button gcButton = (Button) view.findViewById(R.id.gc_button);
        gcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gcWebView.getSettings().setJavaScriptEnabled(true);
                gcWebView.setWebViewClient(new WebViewClient() {
                    public void onPageFinished(WebView view, String url) {
                        progress.hide();
                    }
                    @Override
                    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                        handler.proceed(); // Ignore SSL certificate errors
                    }
                });
                progress=new ProgressDialog(getActivity());
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setMessage("Loading");
                progress.setIndeterminate(true);
                progress.show();
                gcWebView.loadUrl(WelcomeActivity.techGCurl);
                gcWebView.setVerticalScrollBarEnabled(true);
                gcWebView.setHorizontalScrollBarEnabled(true);
            }
        });

        return view;
    }

}
