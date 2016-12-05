package cai288.cs371m.project.customClasses;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Cynthia on 11/9/2016.
 */

public class MovieRecord implements Serializable{
    public interface MovieInformationCallback{
        public void callback();
    }
    private static final String OMDBAPI_BYID = "http://www.omdbapi.com/?i=";
    private String title;
    private String year;
    private String imdbID;
    private String rating;
    private String runtime;
    private String genres;
    private String director;
    private String writers;
    private String plot;
    private String awards;
    private String actors;
    private String metascore;
    private String imdbRating;
    private Bitmap poster;
    public String showID;
    public boolean ready;
    private final static String TAG = "MovieRecord: ";

    public MovieRecord(String title, String year, String imdbID){
        this.title = title;
        this.year = year;
        this.imdbID = imdbID;
        ready = false;

    }

    public MovieRecord(String imdbID, String title){
        this.imdbID = imdbID;
        this.title = title;
        ready = false;
    }

    private void setInformation(JSONObject result) throws JSONException {
        Log.i(TAG, "setting Information");
        if(year == null)
            year = result.getString("Year");
        rating = result.getString("Rated");
        runtime = result.getString("Runtime");
        genres = result.getString("Genre");
        director = result.getString("Director");
        writers = result.getString("Writer");
        actors = result.getString("Actors");
        plot = result.getString("Plot");
        awards = result.getString("Awards");
        metascore = result.getString("Metascore");
        imdbRating = result.getString("imdbRating");
        ready = true;



    }

    public void getInformation(MovieInformationCallback callback){
        new GetInfo(callback).execute(imdbID);

    }

    protected class GetInfo extends AsyncTask<String, Void, JSONObject> {
        private MovieInformationCallback callback;
        public GetInfo(MovieInformationCallback callback){
            this.callback = callback;
        }
        protected JSONObject doInBackground(String... s) {
            String id = s[0];
            String response = "";
            String line = null;
            JSONObject result = null;

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            Bitmap image = null;

            URL searchURL = null;
            String url = OMDBAPI_BYID + id;

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
            try {
                setInformation(result);
                callback.callback();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            synchronized(this) {
                this.notify();
            }
        }

    }

    public String getTitle(){
        return title;
    }

    public String getYear(){
        return year;
    }

    public String getImdbID(){
        return imdbID;
    }

    public String getRating() {
        return rating;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getGenres() {
        return genres;
    }

    public String getDirector() {
        return director;
    }

    public String getWriters() {
        return writers;
    }

    public String getActors(){
        return actors;
    }

    public String getPlot() {
        return plot;
    }

    public String getAwards() {
        return awards;
    }

    public String getMetascore() {
        return metascore;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public String toString(){
        return (title + "(" + year + ")");
    }

    public void setPoster(Bitmap poster){
        this.poster = poster;
    }

    public Bitmap getPoster(){
        return this.poster;
    }

    public boolean isReady(){
        return ready;
    }
}
