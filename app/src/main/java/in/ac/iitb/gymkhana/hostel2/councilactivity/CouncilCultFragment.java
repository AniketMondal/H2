package in.ac.iitb.gymkhana.hostel2.councilactivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import in.ac.iitb.gymkhana.hostel2.R;
import in.ac.iitb.gymkhana.hostel2.councilactivity.CouncilActivity.CouncilMember;
import in.ac.iitb.gymkhana.hostel2.councilactivity.CouncilActivity.CouncilMemberAdapter;

/**
 * Created by bhavesh on 21/09/17.
 */

public class CouncilCultFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.council_fragment_grid, container, false);

        final ArrayList<CouncilMember> members = new ArrayList<CouncilMember>();
        members.add(new CouncilMember(R.drawable.arpit, "Arpit Shashwat", "Cultural Councillor", "213", "8291339774"));
        members.add(new CouncilMember(R.drawable.chahal, "Divyanshu Chahal", "Events Nominee", "17", "8454950371"));
        members.add(new CouncilMember(R.drawable.ayush, "Ayush Raj", "Social Secretary", "207", "9521421205"));
        members.add(new CouncilMember(R.drawable.piyush, "Piyush Kumar", "Social Secretary", "252", "7045563948"));
        members.add(new CouncilMember(R.drawable.tk, "Tamoghno Pramanik", "Music Secretary", "172", "8777036303"));
        members.add(new CouncilMember(R.drawable.pholder, "Shubham Gupta", "Film & Media Secretary", "43", "7404356476"));
        members.add(new CouncilMember(R.drawable.pholder, "Rohit Chaudhary", "Design Secretary", "248", "8340573518"));
        members.add(new CouncilMember(R.drawable.ashish, "Ashish Kumar", "Design Secretary", "22", "8630410295"));
        members.add(new CouncilMember(R.drawable.sumit, "Sumit Patel", "Dance Secretary", "161", "9990260285"));
        members.add(new CouncilMember(R.drawable.malale, "Ashmak Malale", "Speaking Arts Secretary", "284", "9834617718"));
        members.add(new CouncilMember(R.drawable.abhay, "Abhay Kumar", "Dramatics Secretary", "179", "9672660282"));
        members.add(new CouncilMember(R.drawable.soni, "Shubham Soni", "Fine Arts Secretary", "69", "9131046336"));
        members.add(new CouncilMember(R.drawable.kolhe, "Chaitanya Kolhe", "Literary Arts Secretary", "47", "9834025294"));
        members.add(new CouncilMember(R.drawable.pholder, "Kunal Dhruv", "Photography Secretary", "50", "9340473585"));

        CouncilMemberAdapter adapter = new CouncilMemberAdapter(getActivity(), members);
        GridView gridView = (GridView) view.findViewById(R.id.council_grid);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int pos = i;
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + members.get(pos).getPhone()));
                                startActivity(intent);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Call " + members.get(i).getName() + "?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();


            }
        });

        return view;
    }

}
