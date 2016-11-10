package cai288.cs371m.project.customClasses;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import cai288.cs371m.project.R;
import cai288.cs371m.project.activities.MovieInfoActivity;

/**
 * Created by Cynthia on 11/5/2016.
 */

public class DynamicAdapter extends RecyclerView.Adapter<DynamicAdapter.DynamicViewHolder>{

    private ArrayList<String> ids =  new ArrayList<>();
    private HashMap<String, MovieRecord> movieRecords = new HashMap<>();
    private LayoutInflater mInflater;
    private Context context;



    public class DynamicViewHolder extends RecyclerView.ViewHolder{

        public TextView title;
        public View container;

        public DynamicViewHolder(View itemView){
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.movieTitle);
            this.container = itemView;
        }
    }

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
