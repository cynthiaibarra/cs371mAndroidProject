package cai288.cs371m.project.activities;


import android.media.Image;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import cai288.cs371m.project.R;
import cai288.cs371m.project.ViewPagerAdapter;
import cai288.cs371m.project.customClasses.DatabaseManager;
import cai288.cs371m.project.customClasses.MovieRecord;

public class CommonMoviesActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private ViewPagerAdapter adapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_movies);


        ImageButton back = (ImageButton) findViewById(R.id.backArrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        Bundle b = getIntent().getExtras();
        String userEmail = b.getString("userEmail");
        String friendEmail = b.getString("friendEmail");
        String friendName = b.getString("friendName");
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        tabLayout = (TabLayout)  findViewById(R.id.commonMovieTabLayout);
        mViewPager = (ViewPager) findViewById(R.id.container);
        initViewPagerAdapter(userEmail, friendEmail, tabLayout.getSelectedTabPosition());
        TextView commonwith = (TextView) findViewById(R.id.movies_in_common_text);
        commonwith.setText(commonwith.getText() + friendName);

    }

    private void initViewPagerAdapter(String userEmail, String friendEmail, int position) {


        DatabaseManager.getCommonMoviesList(R.string.watch_list, userEmail, friendEmail, new DatabaseManager.getMoviesListener() {
            @Override
            public void getMoviesList(ArrayList<MovieRecord> m) {
                adapter.addFragment(ListsFragment.newInstance(m), "WATCHLIST");
                mViewPager.setAdapter(adapter);
                tabLayout.setupWithViewPager(mViewPager);
            }
        });

        DatabaseManager.getCommonMoviesList(R.string.favorite_list, userEmail, friendEmail, new DatabaseManager.getMoviesListener() {
            @Override
            public void getMoviesList(ArrayList<MovieRecord> movies) {

                adapter.addFragment(ListsFragment.newInstance(movies), "FAVELIST");
                mViewPager.setAdapter(adapter);
                tabLayout.setupWithViewPager(mViewPager);
            }
        });


    }








}
