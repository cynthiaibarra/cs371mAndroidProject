package cai288.cs371m.project.customClasses;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import cai288.cs371m.project.R;
import cai288.cs371m.project.activities.MovieInfoActivity;

/**
 * Created by Cynthia on 11/5/2016.
 */

/*
public class DynamicAdapter extends RecyclerView.Adapter<DynamicAdapter.DynamicViewHolder>{

    private ArrayList<String> ids =  new ArrayList<>();
    private HashMap<String, MovieRecord> movieRecords = new HashMap<>();
    private LayoutInflater mInflater;
    private Context context;




    public DynamicAdapter(Context context){
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;

    }

    @Override
    public DynamicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout, parent, false);
        return new DynamicViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DynamicViewHolder holder, final int position) {
        Log.i("DYNAMIC ADAPTER", Integer.toString(position));
        final MovieRecord movie = getItem(position);
        if(movie.getTitle() == null){
            throw new IllegalStateException("THIS SHOULD NOT HAPPEN");
        }

        holder.title.setText(movie.getTitle());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MovieInfoActivity.class);
                intent.putExtra("movie", (Serializable) movie);
                v.getContext().startActivity(intent);

            }
        });

    }

//    public void changeList(ArrayList<MovieRecord> movieRecords){
//        this.movieRecords = movieRecords;
//        notifyDataSetChanged();
//    }

    public void clear(){
        movieRecords.clear();
        ids.clear();
    }

    public void addItem(MovieRecord movie){
        ids.add(movie.getImdbID());
        movieRecords.put(movie.getImdbID(), movie);
    }

    public void removeItem(String imdbID){
        int index = ids.indexOf(imdbID);
        if(index > -1){
            ids.remove(index);
            notifyDataSetChanged();
            movieRecords.remove(imdbID);
        }


    }

    public boolean contains(String imdbID){
        return movieRecords.containsKey(imdbID);
    }

    @Override
    public int getItemCount() {
        return movieRecords.size();
    }

    private MovieRecord getItem(int position){
        return movieRecords.get(ids.get(position));
    }
}
*/
public class DynamicAdapter extends GenericAdapter<MovieRecord> implements MovieFetch.Callback{
    private boolean grid;
    public DynamicAdapter(boolean grid){
        this.grid = grid;
    }

    private class DynamicViewHolder extends RecyclerViewHolder{
        public TextView title;
        public View container;
        public ImageView poster;

        public DynamicViewHolder(View itemView){
            super(itemView);
            this.container = itemView;
            if(!grid) {
                this.title = (TextView) itemView.findViewById(R.id.movieTitle);
            }else{
                this.title = (TextView) itemView.findViewById(R.id.movie_title);
                this.poster = (ImageView) itemView.findViewById(R.id.movie_poster);
            }
        }

        @Override
        public void onClick(View v) {


        }


    }

    public void removeItem(String n){
        int i = 0;
        for(MovieRecord m: list){
            if(m.getTitle().equals(n)){
                list.remove(i);
                notifyItemRemoved(i);
                break;
            }
            i++;
        }
    }

    public boolean contains(String title){
        for (MovieRecord m: list){
            if (m.getTitle().equals(title))
                    return true;
        }
        return false;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if(!grid){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_layout, parent, false);
        }else{
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.movie_item_grid_layout, parent, false);
        }

        return new DynamicViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GenericAdapter.RecyclerViewHolder holder, int position) {
        DynamicViewHolder h = (DynamicViewHolder) holder;
        final MovieRecord movie = getItem(position);
        if(movie.getTitle() == null){
            throw new IllegalStateException("THIS SHOULD NOT HAPPEN");
        }

        h.title.setText(movie.getTitle());
        h.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MovieInfoActivity.class);
                intent.putExtra("movie", (Serializable) movie);
                v.getContext().startActivity(intent);

            }
        });
        if(grid){
            new MovieFetch(h.poster).fetch(MovieFetch.IMAGE, movie.getImdbID());
        }

    }
    @Override
    public void fetchImageComplete(Bitmap image) {

    }

    @Override
    public void fetchMoviesComplete(JSONObject result) {

    }




}