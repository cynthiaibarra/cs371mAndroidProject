package cai288.cs371m.project.activities;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import cai288.cs371m.project.customClasses.DynamicAdapter;
import cai288.cs371m.project.customClasses.MovieFetch;
import cai288.cs371m.project.R;
import cai288.cs371m.project.customClasses.MovieRecord;

public class SearchActivity extends AppCompatActivity implements MovieFetch.Callback, View.OnClickListener{

    private RecyclerView rv;
    private DynamicAdapter adapter;
    private DatabaseReference databaseReference;
    private EditText query;
    private MovieFetch movieFetcher;
    private final String TAG = "SearchActivity: ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.searchToolbar);
        setSupportActionBar(toolbar);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();



        ImageButton back = (ImageButton) findViewById(R.id.backArrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        movieFetcher = new MovieFetch(this);
        query = (EditText) findViewById(R.id.searchQuery);


        ImageButton searchBtn = (ImageButton) findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(this);
            //TODO dim search button until it contains an entry


        rv = (RecyclerView) findViewById(R.id.searchRecyclerView);
        LinearLayoutManager rv_layout_mgr = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(rv_layout_mgr);
        rv.setItemAnimator(new DefaultItemAnimator());
        adapter = new DynamicAdapter(this);
        rv.setAdapter(adapter);

//        movieFetcher.fetch(MovieFetch.DISCOVER, "");


    }

    @Override
    public void fetchMoviesComplete(JSONObject result) {
        JSONArray movies = null;
        adapter.clear();
        try{
            movies = result.getJSONArray("Search");
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        if(movies != null){
            for(int i = 0; i < movies.length(); i++){
                JSONObject movie = null;
                String title = null, year = null, imdbID = null;
                MovieRecord record;
                try{
                    movie = movies.getJSONObject(i);
                    title = movie.getString("Title");
                    year =  movie.getString("Year");
                    imdbID = movie.getString("imdbID");
                } catch (Exception e){
                    Log.e(TAG, e.toString());
                }
                if (movie != null && title != null & year != null && imdbID != null){
                    adapter.addItem(new MovieRecord(title, year, imdbID));
                    adapter.notifyItemInserted(adapter.getItemCount() - 1);
                }
            }
        }


    }

    @Override
    public void fetchImageComplete(Bitmap b) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.searchBtn:
                String q = query.getText().toString();
                if (q.length() == 0) {
                    Toast.makeText(v.getContext(), "Please enter a search query.", Toast.LENGTH_SHORT).show();
                } else {
                    movieFetcher.fetch(MovieFetch.SEARCH, q);
                }
                break;
            default:
                break;
        }

    }
}
