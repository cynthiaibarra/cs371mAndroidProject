package cai288.cs371m.project.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import cai288.cs371m.project.customClasses.MovieFetch;
import cai288.cs371m.project.R;
import cai288.cs371m.project.customClasses.MovieRecord;
public class MovieInfoActivity extends AppCompatActivity implements MovieFetch.Callback,
    View.OnClickListener{

    public static final String TAG = "MovieInfoActivity: ";
    public static final String NETFLIX_API = "http://netflixroulette.net/api/api.php?title=";
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
        if(movie.showID == null)
            new GetNetflixInfo().execute(movie.getTitle());
        if(movie.getPoster() == null)
            new MovieFetch(this).fetch(MovieFetch.IMAGE, movie.getImdbID());
        else
            setPoster();
        if (!movie.ready){
            movie.getInformation(new MovieRecord.MovieInformationCallback() {
                @Override
                public void callback() {
                    initDisplay();

                }
            });
        }else{
            initDisplay();
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
        String overview =  movie.getPlot();
        movieHomepage.setText(overview);

        TextView runtime = (TextView) findViewById(R.id.runtime);
        TextView genre = (TextView) findViewById(R.id.genre);
        TextView cast = (TextView) findViewById(R.id.cast);
        TextView director = (TextView) findViewById(R.id.director);
        genre.setText(movie.getGenres().replace(",", " |"));
        runtime.setText(runtime.getText() + movie.getRuntime() + " | Rated: " + movie.getRating());
        cast.setText(movie.getActors());
        director.setText(movie.getDirector());
    }

    @Override
    public void fetchMoviesComplete(JSONObject result) {

    }

    @Override
    public void fetchImageComplete(Bitmap image) {
        movie.setPoster(image);
        setPoster();

    }
    private void setPoster(){
        progressBar.setVisibility(View.GONE);
        moviePoster.setVisibility(View.VISIBLE);
        if(movie.getPoster() == null)
            moviePoster.setImageResource(R.drawable.noposter);
        else{
            moviePoster.setImageBitmap(movie.getPoster());
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

    private class GetNetflixInfo extends AsyncTask<String, Void, JSONObject> {
        protected JSONObject doInBackground(String... s) {
            String response = "";
            String line;
            JSONObject result = null;
            String query;
            try {
                query = URLEncoder.encode(s[0], "UTF-8");
            } catch (Exception e){
                throw new IllegalStateException("WTF utf-8");
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            URL searchURL;
            String url = NETFLIX_API +  query;
            Log.i(TAG, url);

            try {
                searchURL = new URL(url);
            } catch (Exception e) {
                return null;
            }

            try {
                urlConnection = (HttpURLConnection) searchURL.openConnection();
                urlConnection.setRequestMethod("GET");
                inputStream = urlConnection.getInputStream();
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = streamReader.readLine()) != null) {
                    response += line;
                }
            } catch (Exception e) {
                return null;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                        urlConnection.disconnect();
                    } catch (Exception ignored) {

                    }
                }

            }
            if(response != null){
                Log.i(TAG, response);
                try{
                    result = (JSONObject) new JSONTokener(response).nextValue();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return result;
        }

        protected void onPostExecute(JSONObject result) {
            if(result != null){
                Log.i(TAG, result.toString());
                if(result.has("show_id")){
                    try {
                        movie.showID = result.getString("show_id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ImageButton netflix_btn = (ImageButton) findViewById(R.id.netflix_btn);
                    netflix_btn.setImageResource(R.drawable.netflix);
                    netflix_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String watchUrl = "http://www.netflix.com/watch/" + movie.showID;
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setClassName("com.netflix.mediaclient", "com.netflix.mediaclient.ui.launch.UIWebViewActivity");
                                intent.setData(Uri.parse(watchUrl));
                                startActivity(intent);
                            }
                            catch(Exception e)
                            {
                                // netflix app isn't installed, send to website.
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(watchUrl));
                                startActivity(intent);
                            }
                        }
                    });
                }else{
                    movie.showID = "";
                }

            }

        }

    }
}

