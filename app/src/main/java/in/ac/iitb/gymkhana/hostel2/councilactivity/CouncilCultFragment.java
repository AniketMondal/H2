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
        members.add(new CouncilMember(R.drawable.logo, "Bhavesh", "Web Secretary", "189", "7696464483"));
        members.add(new CouncilMember(R.drawable.logo, "Aditya", "Web Secretary", "280", "pta nhi"));
        members.add(new CouncilMember(R.drawable.logo, "Dhruv", "Cat Secretary", "189", "8454953889"));
        members.add(new CouncilMember(R.drawable.logo, "Sourabh", "Aalas Secretary", "188", "pta nhi"));

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
