package cai288.cs371m.project.customClasses;

import android.content.ClipData;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.net.URL;
import java.util.ArrayList;

import cai288.cs371m.project.R;

/**
 * Created by Cynthia on 11/11/2016.
 * REFERENCE: http://stackoverflow.com/questions/37482786/how-to-use-one-recyclerview-adapter-for-objects-of-different-types-using-generic
 */

public abstract class GenericAdapter<T> extends RecyclerView.Adapter<GenericAdapter.RecyclerViewHolder>{
    protected ArrayList<T> list = new ArrayList<T>();

    public T getItem(int position){
        return list.get(position);
    }

    public void setItems(ArrayList<T> items){
        this.list.clear();
        this.list = items;
        notifyDataSetChanged();
    }

    public void addItem(T item){
        list.add(item);
        notifyItemInserted(getItemCount() - 1);
    }

    public boolean contains(T item){
        return list.indexOf(item) > -1;
    }

    public void removeItem(T item){
        int index = list.indexOf(item);
        if(index > -1){
            list.remove(item);
            notifyItemRemoved(index);
        }
    }

    protected void removeItem(int position){
        if (position > 0 && position < getItemCount()){
            list.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void setList(ArrayList<T> list){
        this.setItems(list);
    }

    public void clear(){
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount(){
        return list.size();
    }


    public abstract class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public abstract void onClick(View v);

    }
}
