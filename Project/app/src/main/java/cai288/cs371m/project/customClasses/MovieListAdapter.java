package cai288.cs371m.project.customClasses;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cai288.cs371m.project.R;

/**
 * Created by Cynthia on 11/27/2016.
 */

public class MovieListAdapter extends GenericAdapter<String> {


    public MovieListAdapter(){
        super();
        Log.i("movielistadapter", "created");
    }


    public class MovieHolder extends RecyclerViewHolder{
        protected View container;
        protected TextView title;

        public MovieHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.movieTitle);
            container = itemView;

        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout, parent, false);
        return new MovieHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GenericAdapter.RecyclerViewHolder holder, int position) {
        String title = getItem(position);
        Log.i("movielistadapter", title);
        MovieHolder h = (MovieHolder) holder;
        h.title.setText(title);
    }
}
