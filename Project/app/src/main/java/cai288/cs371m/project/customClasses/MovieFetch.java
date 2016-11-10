package cai288.cs371m.project.customClasses;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.IllegalFormatCodePointException;

/**
 * Created by Cynthia on 11/7/2016.
 */


public class MovieFetch {
    public static final String TAG = "MovieFetch";

    public interface Callback {
        void fetchMoviesComplete(JSONObject result);
        void fetchImageComplete(Bitmap image);
    }
    public static final int DISCOVER = 1;
    public static final int SEARCH = 2;
    public static final int IMAGE = 3;
    private static final String OMDBAPI_SEARCH = "http://www.omdbapi.com/?type=movie&s=";
    private static final String OMDBAPI_POSTER = "http://img.omdbapi.com/?apikey=73bf5f47&w=200&i=";

    protected Callback callback = null;

    public MovieFetch(Callback callback){
        this.callback = callback;
    }

    public void fetch(int call, String query){
        switch (call) {
//            case DISCOVER:
//                new GetDiscover().execute();
 //               break;
            case SEARCH:
                new GetSearch().execute(query);
                break;
            case IMAGE:
                new GetImage().execute(query);
            default:
                break;
        }
    }
    private class GetSearch extends AsyncTask<String, Void, JSONObject> {
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
            String url = OMDBAPI_SEARCH +  query;
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
            callback.fetchMoviesComplete(result);
        }

    }
//    private class GetDiscover extends AsyncTask<Void, Void, ArrayList<MovieDb>> {
//        protected ArrayList<MovieDb> doInBackground(Void... v) {
//            Discover search = new Discover();
//            search.page(1);
//            search.language("en");
//            search.includeAdult(true);
//            search.sortBy("popularity.desc");
//            MovieResultsPage page = new TmdbApi(Global.TMDBkey).getDiscover().getDiscover(search);
//            ArrayList<MovieDb> movies = (ArrayList<MovieDb>) page.getResults();
//            return movies;
//        }
//
//        protected void onPostExecute(ArrayList<MovieDb> movies) {
//            callback.fetchMoviesComplete(movies);
//        }
//
//    }

    private class GetImage extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... s) {
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            Bitmap image = null;

            URL searchURL = null;
            String url = OMDBAPI_POSTER + s[0];
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
                image = BitmapFactory.decodeStream(inputStream);
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
            return image;
        }


        protected void onPostExecute(Bitmap b) {
            callback.fetchImageComplete(b);
        }
    }
}
