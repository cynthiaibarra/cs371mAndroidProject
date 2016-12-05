package cai288.cs371m.project.activities;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.json.JSONArray;
import org.json.JSONObject;
import cai288.cs371m.project.customClasses.AppUser;
import cai288.cs371m.project.customClasses.DatabaseManager;
import cai288.cs371m.project.customClasses.DynamicAdapter;
import cai288.cs371m.project.customClasses.FriendAdapter;
import cai288.cs371m.project.customClasses.MovieFetch;
import cai288.cs371m.project.R;
import cai288.cs371m.project.customClasses.MovieRecord;

public class SearchActivity extends AppCompatActivity implements MovieFetch.Callback, View.OnClickListener{
    public static final int SEARCH_MOVIES = 0;
    public static final int SEARCH_FRIENDS = 1;
    private RecyclerView rv;
    private DynamicAdapter adapter;
    private DatabaseReference databaseReference;
    private EditText query;
    private MovieFetch movieFetcher;
    private final String TAG = "SearchActivity: ";
    private String previousQuery;
    private ProgressBar progressBar;
    private int searchType;
    private FriendAdapter friendAdapter;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.searchToolbar);
        setSupportActionBar(toolbar);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressBar = (ProgressBar) findViewById(R.id.search_progressBar);

        searchType = 0;
        rv = (RecyclerView) findViewById(R.id.searchRecyclerView);
        LinearLayoutManager rv_layout_mgr = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(rv_layout_mgr);
        rv.setItemAnimator(new DefaultItemAnimator());

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            searchType = bundle.getInt("searchType");
        }

        query = (EditText) findViewById(R.id.searchQuery);
        switch (searchType){
            case SEARCH_MOVIES:
                adapter = new DynamicAdapter(false);
                rv.setAdapter(adapter);
                movieFetcher = new MovieFetch(this);
                break;
            case SEARCH_FRIENDS:
                query.setHint("Search user by email");
                friendAdapter = new FriendAdapter(FriendAdapter.TYPE_ADD_FRIEND);
                rv.setAdapter(friendAdapter);
                break;
            default:
                break;

        }

        ImageButton back = (ImageButton) findViewById(R.id.backArrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        query.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });





        ImageButton searchBtn = (ImageButton) findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(this);
        //TODO dim search button until it contains an entry




//        movieFetcher.fetch(MovieFetch.DISCOVER, "");


    }

    @Override
    public void fetchMoviesComplete(JSONObject result) {
        progressBar.setVisibility(View.INVISIBLE);
        rv.setVisibility(View.VISIBLE);
        Log.i(TAG, result.toString());
        JSONArray movies = null;
        String error = null;
        try{
            if(result.has("Search"))
                movies = result.getJSONArray("Search");
            else if(result.has("Error"))
                error = result.getString("Error");
        }catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if(error != null) {
            Toast.makeText(this, "Error: " + error + ". Try another query.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(movies != null){
            if(movies.length() == 0){
                Toast.makeText(this, "Search yielded no results. Try shortening query.", Toast.LENGTH_SHORT).show();
                return;
            }
            for(int i = 0; i < movies.length(); i++){
                JSONObject movie = null;
                String title = null, year = null, imdbID = null;
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

    private void search(){
        String q = query.getText().toString().trim();
        if(previousQuery != null && previousQuery.equals(q)) {
            Toast.makeText(this, "Already displaying results for query.", Toast.LENGTH_SHORT).show();
            return;
        }
        previousQuery = q;
        if (q.length() == 0) {
            Toast.makeText(this, "Please enter a search query.", Toast.LENGTH_SHORT).show();
        }else{

            rv.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            switch (searchType){
                case SEARCH_MOVIES:
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    movieFetcher.fetch(MovieFetch.SEARCH, q);
                    break;
                case SEARCH_FRIENDS:
                    Log.i(TAG, "SEARCH FRIENDS");
                    friendAdapter.clear();
                    String searchQ = q.replace(".", "_");
                    if(searchQ.contains("@"))
                        DatabaseManager.getUser(searchQ, new addUserToFriendAdapter());
                    else
                        DatabaseManager.getUserByName(searchQ, new addUserToFriendAdapter());
                    progressBar.setVisibility(View.INVISIBLE);
                    rv.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private class addUserToFriendAdapter implements DatabaseManager.getUserListener{

        @Override
        public void getUserCallback(AppUser user) {
            if(user == null)
                Toast.makeText(SearchActivity.this, "Search yielded no results.", Toast.LENGTH_SHORT).show();
            else
                friendAdapter.addItem(user);
        }
    }

    @Override
    public void fetchImageComplete(Bitmap b) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.searchBtn:
                search();
                break;
            default:
                break;
        }

    }
}
