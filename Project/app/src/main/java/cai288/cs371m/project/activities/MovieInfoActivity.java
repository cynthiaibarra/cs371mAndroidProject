package cai288.cs371m.project.activities;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;
import java.util.HashMap;

import cai288.cs371m.project.customClasses.MovieFetch;
import cai288.cs371m.project.R;
import cai288.cs371m.project.customClasses.MovieRecord;
public class MovieInfoActivity extends AppCompatActivity implements MovieFetch.Callback,
    View.OnClickListener{

    public static final String TAG = "MovieInfoActivity: ";
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private ImageButton bookmark;
    private ImageButton heart;
    private ImageButton back;
    private MovieRecord movie;
    private boolean bookmarked;
    private boolean hearted;
    private String watchList;
    private String favoriteList;
    private ProgressBar progressBar;
    ImageView moviePoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.movieInfoToolbar);
        setSupportActionBar(toolbar);
        initVariables();

        final Query bookmarkQ = databaseReference.child("lists").child(watchList).orderByKey().equalTo(movie.getImdbID());
        bookmarkQ.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.getValue();
                 if(map != null)
                     bookmarked = map.containsKey(movie.getImdbID());
                 if(bookmarked)
                     bookmark.setImageResource(R.drawable.bookmark_plus_highlighted);
             }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
         });

        Query heartQ = databaseReference.child("lists").child(favoriteList).orderByKey().equalTo(movie.getImdbID());
        heartQ.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // String map = (String) dataSnapshot.getValue();
                HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.getValue();
                if(map != null)
                    hearted = map.containsKey(movie.getImdbID());
                if(hearted)
                    heart.setImageResource(R.drawable.heart);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        initDisplay();

    }

    private void initVariables(){
        hearted = false;
        bookmarked = false;
        user = FirebaseAuth.getInstance().getCurrentUser();
        heart = (ImageButton) findViewById(R.id.movieInfo_heart);
        heart.setOnClickListener(this);
        moviePoster = (ImageView) findViewById(R.id.movieInfo_pic);
        back = (ImageButton) findViewById(R.id.movieinfo_back);
        back.setOnClickListener(this);
        bookmark = (ImageButton) findViewById(R.id.movieInfo_bookmarkBtn);
        bookmark.setOnClickListener(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Bundle bundle = getIntent().getExtras();
        movie = null;
        if (bundle != null){
            movie = (MovieRecord) bundle.getSerializable("movie");
        } else {
            Toast.makeText(this, "An error occurred.", Toast.LENGTH_SHORT).show();
            finish();
        }
        String email = user.getEmail().replace(".", "_");
        watchList = email + getString(R.string.watch_list);
        favoriteList = email + getString(R.string.favorite_list);
        progressBar = (ProgressBar) findViewById(R.id.movieInfo_progressBar);
    }

    private void initDisplay(){
        TextView movieTitle = (TextView) findViewById(R.id.movieInfo_title);
        TextView movieHomepage = (TextView) findViewById(R.id.movieInfo_homepage);
        String title = movie.getTitle() + " (" + movie.getYear() + ")";
        movieTitle.setText(title);
        new MovieFetch(this).fetch(MovieFetch.IMAGE, movie.getImdbID());
        String overview = "\n" + movie.getPlot();
        movieHomepage.setText(overview);
    }

    @Override
    public void fetchMoviesComplete(JSONObject result) {

    }

    @Override
    public void fetchImageComplete(Bitmap image) {
        progressBar.setVisibility(View.GONE);
        moviePoster.setVisibility(View.VISIBLE);
        if(image == null)
            moviePoster.setImageResource(R.drawable.noposter);
        else{
            moviePoster.setImageBitmap(image);
        }

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.movieInfo_bookmarkBtn:
                if(!bookmarked) {
                    this.databaseReference.child("lists").child(watchList).child(movie.getImdbID()).setValue(movie.getTitle());
                    bookmark.setImageResource(R.drawable.bookmark_plus_highlighted);
                    bookmarked = true;
                }
                else{
                    this.databaseReference.child("lists").child(watchList).child(movie.getImdbID()).removeValue();
                    bookmarked = false;
                    bookmark.setImageResource(R.drawable.bookmark_plus_unhighlighted);
                }
                break;
            case R.id.movieInfo_heart:
                if(!hearted) {
                    this.databaseReference.child("lists").child(favoriteList).child(movie.getImdbID()).setValue(movie.getTitle());
                    heart.setImageResource(R.drawable.heart);
                    hearted = true;
                }
                else{
                    this.databaseReference.child("lists").child(favoriteList).child(movie.getImdbID()).removeValue();
                    hearted = false;
                    heart.setImageResource(R.drawable.heart_outline);
                }
                break;
            case R.id.movieinfo_back:
                finish();
                break;
            default:
                break;
        }
    }

}

