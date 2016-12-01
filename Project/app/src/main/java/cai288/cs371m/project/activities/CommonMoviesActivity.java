package cai288.cs371m.project.activities;

import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cai288.cs371m.project.R;
import cai288.cs371m.project.ViewPagerAdapter;
import cai288.cs371m.project.customClasses.DatabaseManager;

public class CommonMoviesActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private ViewPagerAdapter adapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_movies);

        Toolbar toolbar = (Toolbar) findViewById(R.id.cToolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        Bundle b = getIntent().getExtras();
        String userEmail = b.getString("userEmail");
        String friendEmail = b.getString("friendEmail");
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        tabLayout = (TabLayout)  findViewById(R.id.commonMovieTabLayout);
        mViewPager = (ViewPager) findViewById(R.id.container);
        initViewPagerAdapter(userEmail, friendEmail, tabLayout.getSelectedTabPosition());



        // Set up the ViewPager with the sections adapter.

        ;






    }

    private void initViewPagerAdapter(String userEmail, String friendEmail, int position) {


        DatabaseManager.getCommonMoviesList(R.string.watch_list, userEmail, friendEmail, new DatabaseManager.getMoviesListener() {
            @Override
            public void getMoviesList(ArrayList<String> movies) {
                Toast.makeText(CommonMoviesActivity.this, movies.toString(), Toast.LENGTH_SHORT).show();
                adapter.addFragment(ListsFragment.newInstance(movies), "WATCHLIST");
                mViewPager.setAdapter(adapter);
                tabLayout.setupWithViewPager(mViewPager);
            }
        });

        DatabaseManager.getCommonMoviesList(R.string.favorite_list, userEmail, friendEmail, new DatabaseManager.getMoviesListener() {
            @Override
            public void getMoviesList(ArrayList<String> movies) {
                Toast.makeText(CommonMoviesActivity.this, movies.toString(), Toast.LENGTH_SHORT).show();
                adapter.addFragment(ListsFragment.newInstance(movies), "FAVELIST");
                mViewPager.setAdapter(adapter);
                tabLayout.setupWithViewPager(mViewPager);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_common_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.clear) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




}
