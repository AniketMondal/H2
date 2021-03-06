package in.ac.iitb.gymkhana.hostel2.homeactivity;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import in.ac.iitb.gymkhana.hostel2.R;
import in.ac.iitb.gymkhana.hostel2.WelcomeActivity;

/**
 * Created by bhavesh on 21/09/17.
 */

public class HomeCalendarFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_calendar, container, false);

        ImageView calendar = (ImageView) view.findViewById(R.id.calendar);
        calendar.setImageBitmap(WelcomeActivity.calendar);

        return view;
    }

}
