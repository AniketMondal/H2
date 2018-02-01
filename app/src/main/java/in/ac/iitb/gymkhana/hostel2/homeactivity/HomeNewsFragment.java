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

import in.ac.iitb.gymkhana.hostel2.CacheManager;
import in.ac.iitb.gymkhana.hostel2.R;
import in.ac.iitb.gymkhana.hostel2.homeactivity.HomeActivity.ExpandableItem;
import in.ac.iitb.gymkhana.hostel2.homeactivity.HomeActivity.ExpandableItemAdapter;


/**
 * Created by bhavesh on 21/09/17.
 */

public class HomeNewsFragment extends Fragment {

    CacheManager cache;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_list, container, false);

        cache = new CacheManager(getActivity().getApplicationContext());

        final ArrayList<ExpandableItem> newsList = new ArrayList<ExpandableItem>();

        try {
            JSONObject complete = new JSONObject(cache.getNews());
            JSONArray jsonArray = complete.getJSONArray("NEWS");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject newsItem = jsonArray.getJSONObject(i);
                newsList.add(new ExpandableItem(newsItem.getString("BRIEF"), newsItem.getString("CONTENT"), expanded(i)));
            }
        } catch (Exception e) {}


        ExpandableItemAdapter adapter = new ExpandableItemAdapter(getActivity(), newsList);
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
        if (i == 0) return true;
        else return false;
    }

}
