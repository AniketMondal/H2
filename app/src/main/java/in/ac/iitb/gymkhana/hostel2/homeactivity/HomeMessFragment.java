package in.ac.iitb.gymkhana.hostel2.homeactivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.ac.iitb.gymkhana.hostel2.R;
import in.ac.iitb.gymkhana.hostel2.homeactivity.HomeActivity.ExpandableItem;
import in.ac.iitb.gymkhana.hostel2.homeactivity.HomeActivity.ExpandableItemAdapter;

import static in.ac.iitb.gymkhana.hostel2.WelcomeActivity.messMenu;

/**
 * Created by bhavesh on 21/09/17.
 */

public class HomeMessFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_list, container, false);

        final ArrayList<ExpandableItem> menuList = new ArrayList<ExpandableItem>();
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        try {
            JSONObject complete = new JSONObject(messMenu);
            JSONArray jsonArray = complete.getJSONArray("DAY");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject menuItem = jsonArray.getJSONObject(i);
                StringBuilder menu = new StringBuilder();
                menu.append("BREAKFAST:\n");
                menu.append(menuItem.getString("BREAKFAST").trim());
                menu.append("\n\nLUNCH:\n");
                menu.append(menuItem.getString("LUNCH").trim());
                menu.append("\n\nTIFFIN:\n");
                menu.append(menuItem.getString("TIFFIN").trim());
                menu.append("\n\nDINNER:\n");
                menu.append(menuItem.getString("DINNER").trim());
                menuList.add(new ExpandableItem(days[i], menu.toString()));
            }
        } catch (Exception e) {}


        ExpandableItemAdapter adapter = new ExpandableItemAdapter(getActivity(), menuList);
        ListView listView = (ListView) view.findViewById(R.id.expandable_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView content = (TextView) view.findViewById(R.id.expandable_content);
                ImageView arrow = (ImageView) view.findViewById(R.id.expandable_arrow);
                if(content.isShown()){
                    content.setVisibility(View.GONE);
                    content.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_up));
                    arrow.setImageResource(R.drawable.down_arrow);
                }
                else{
                    content.setVisibility(View.VISIBLE);
                    content.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_down));
                    arrow.setImageResource(R.drawable.up_arrow);
                }
            }
        });

        return view;
    }

}
