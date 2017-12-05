package in.ac.iitb.gymkhana.hostel2.infoactivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
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

public class InfoCultFragment extends Fragment {

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
        infoText.setText("Cultural Activities have been an important part of life at Hostel 2. Hostel 2 boasts of having some of the best dancers, musicians, actors, writers, quizzers, speakers, and artists in the institute. Hostel 2 has been in top two positions in Performing Arts Festival a.k.a. PAF for the last few years. The winner of the first Hostel cup and having been positioned fourth in overall general championship last year, H2 continues its path to glory this year too.");

        carouselViewPager = (ViewPager) view.findViewById(R.id.carousel_viewpager);
        sliderDotspanel = (LinearLayout) view.findViewById(R.id.carousel_dots);

        CarouselAdapter carouselAdapter = new CarouselAdapter(getContext(),  new Integer[]{R.drawable.c1, R.drawable.c2, R.drawable.c3, R.drawable.c4});
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
                        progress.dismiss();
                    }
                    @Override
                    public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                progress=new ProgressDialog(getActivity());
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setMessage("Loading");
                progress.setIndeterminate(true);
                progress.show();
                gcWebView.loadUrl(WelcomeActivity.cultGCurl);
                gcWebView.setVerticalScrollBarEnabled(true);
                gcWebView.setHorizontalScrollBarEnabled(true);
            }
        });

        return view;
    }

}