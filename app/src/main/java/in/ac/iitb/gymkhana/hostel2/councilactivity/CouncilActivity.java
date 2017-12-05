package in.ac.iitb.gymkhana.hostel2.councilactivity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import in.ac.iitb.gymkhana.hostel2.CommonFunctions;
import in.ac.iitb.gymkhana.hostel2.R;

public class CouncilActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.include_tabbed_content).setVisibility(View.VISIBLE);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        CouncilAdapter councilAdapter = new CouncilAdapter(getSupportFragmentManager());
        viewPager.setAdapter(councilAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        CommonFunctions.setUser(this);
    }

    private static long back_pressed;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (back_pressed + 2000 > System.currentTimeMillis()){
                super.onBackPressed();
            }
            else{
                Toast.makeText(getBaseContext(), "Press twice to exit", Toast.LENGTH_SHORT).show();
                back_pressed = System.currentTimeMillis();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return CommonFunctions.navigationItemSelect(item, this);
    }



    class CouncilAdapter extends FragmentPagerAdapter {

        public CouncilAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) return new CouncilHeadsFragment();
            else if (position == 1) return new CouncilWebFragment();
            else if (position == 2) return new CouncilMaintFragment();
            else if (position == 3) return new CouncilMessFragment();
            else if (position == 4) return new CouncilCultFragment();
            else if (position == 5) return new CouncilSportsFragment();
            else if (position == 6) return new CouncilTechFragment();
            else return new CouncilAlumniFragment();
        }

        @Override
        public int getCount() {
            return 8;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) return "Heads";
            else if (position == 1) return "Web";
            else if (position == 2) return "Maint";
            else if (position == 3) return "Mess";
            else if (position == 4) return "Cult";
            else if (position == 5) return "Sports";
            else if (position == 6) return "Tech";
            else return "Alumni";
        }
    }



    public static class CouncilMember {

        private int photoID;
        private String name;
        private String post;
        private String room;
        private String phone;

        public CouncilMember(int photoID, String name, String post, String room, String phone){
            this.photoID = photoID;
            this.name = name;
            this.post = post;
            this.room = room;
            this.phone = phone;
        }

        public int getPhotoID() { return photoID; }
        public String getName() { return name; }
        public String getPost() { return post; }
        public String getRoom() { return room; }
        public String getPhone() { return phone; }

    }






    public static class CouncilMemberAdapter extends ArrayAdapter<CouncilMember> {

        public CouncilMemberAdapter(Context context, ArrayList<CouncilMember> members) {
            super(context, 0, members);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View listItemView = convertView;

            if(listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.council_member, parent, false);
            }

            CouncilMember currentMember = getItem(position);

            ImageView photo = (ImageView) listItemView.findViewById(R.id.council_member_photo);
            TextView name = (TextView) listItemView.findViewById(R.id.council_member_name);
            TextView post = (TextView) listItemView.findViewById(R.id.council_member_post);
            TextView room = (TextView) listItemView.findViewById(R.id.council_member_room);
            TextView phone = (TextView) listItemView.findViewById(R.id.council_member_phone);

            photo.setImageResource(currentMember.getPhotoID());
            name.setText(currentMember.getName());
            post.setText(currentMember.getPost());
            room.setText(currentMember.getRoom());
            phone.setText(currentMember.getPhone());

            return listItemView;
        }

    }
}
