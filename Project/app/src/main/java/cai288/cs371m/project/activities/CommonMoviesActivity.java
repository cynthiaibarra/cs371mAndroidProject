package cai288.cs371m.project.activities;


import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

        if (id == R.id.clear) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }






}
