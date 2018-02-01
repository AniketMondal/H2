package in.ac.iitb.gymkhana.hostel2.homeactivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
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
import java.util.Calendar;

import in.ac.iitb.gymkhana.hostel2.CacheManager;
import in.ac.iitb.gymkhana.hostel2.R;
import in.ac.iitb.gymkhana.hostel2.homeactivity.HomeActivity.ExpandableItem;
import in.ac.iitb.gymkhana.hostel2.homeactivity.HomeActivity.ExpandableItemAdapter;


/**
 * Created by bhavesh on 21/09/17.
 */

public class HomeMessFragment extends Fragment {

    CacheManager cache;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_list, container, false);

        cache = new CacheManager(getActivity().getApplicationContext());

        final ArrayList<ExpandableItem> menuList = new ArrayList<ExpandableItem>();
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        String week = "";

        try {
            JSONObject complete = new JSONObject(cache.getMenu());
            JSONArray jsonArray = complete.getJSONArray("DAY");
            week = week+jsonArray.getJSONObject(0).getString("WEEK").toUpperCase();
            //menuList.add(new ExpandableItem("Week",jsonArray.getJSONObject(0).getString("WEEK").toUpperCase(),true));
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
                menuList.add(new ExpandableItem(days[i], menu.toString().toUpperCase(), expanded(i)));
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

        View weekView = getActivity().getLayoutInflater().inflate(R.layout.home_expandable_list_item, null, false);
        weekView.findViewById(R.id.expandable_arrow).setVisibility(View.GONE);
        ((TextView) weekView.findViewById(R.id.expandable_title)).setText(week.toUpperCase());
        ((TextView) weekView.findViewById(R.id.expandable_title)).setGravity(Gravity.CENTER_HORIZONTAL);
        weekView.setOnClickListener(null);
        listView.addHeaderView(weekView,null,false);

        TextView lastSynced = new TextView(getContext());
        lastSynced.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
        lastSynced.setPadding(8,8,8,8);
        lastSynced.setGravity(Gravity.CENTER_HORIZONTAL);
        lastSynced.setTextSize(15);
        lastSynced.setTypeface(lastSynced.getTypeface(), Typeface.ITALIC);
        lastSynced.setText("Last Synced at " + cache.getTime());
        listView.addFooterView(lastSynced,null,false);

        return view;
    }

    boolean expanded(int i) {
        Calendar now = Calendar.getInstance();
        int day = now.get(Calendar.DAY_OF_WEEK);
        if (i == 0 && day == Calendar.MONDAY) return true;
        if (i == 1 && day == Calendar.TUESDAY) return true;
        if (i == 2 && day == Calendar.WEDNESDAY) return true;
        if (i == 3 && day == Calendar.THURSDAY) return true;
        if (i == 4 && day == Calendar.FRIDAY) return true;
        if (i == 5 && day == Calendar.SATURDAY) return true;
        if (i == 6 && day == Calendar.SUNDAY) return true;
        return false;
    }

}
