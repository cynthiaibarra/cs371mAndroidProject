package cai288.cs371m.project.activities;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import cai288.cs371m.project.R;
import cai288.cs371m.project.customClasses.DynamicAdapter;
import cai288.cs371m.project.customClasses.GenericAdapter;
import cai288.cs371m.project.customClasses.MovieListAdapter;
import cai288.cs371m.project.customClasses.MovieRecord;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *  interface
 * to handle interaction events.
 * Use the {@link ListsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListsFragment extends Fragment {
    private DynamicAdapter adapter;
    private MovieListAdapter adapter2;
    private String list;
    private ArrayList<String> movieList;
    private String TAG = "ListsFragment: ";

    // private OnFragmentInteractionListener mListener;

    public ListsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ListsFragment newInstance(String list) {
        ListsFragment fragment = new ListsFragment();
        Bundle args = new Bundle();
        args.putString("list", list);
        fragment.setArguments(args);
        return fragment;
    }

    public static ListsFragment newInstance(ArrayList<String> movieList) {
        ListsFragment fragment = new ListsFragment();
        Bundle args = new Bundle();
        args.putSerializable("movieList", movieList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = getArguments() != null ? getArguments().getString("list") : null;
        movieList = getArguments() != null ? (ArrayList<String>) getArguments().getSerializable("movieList") : null;



    }

    private void setUpRecyclerView(View v) {
        RecyclerView rv = (RecyclerView) v.findViewById(R.id.fragmentRecyclerView);
        LinearLayoutManager rv_layout_mgr = new LinearLayoutManager(getContext());
        rv.setLayoutManager(rv_layout_mgr);
        rv.setItemAnimator(new DefaultItemAnimator());
        if(list != null){
            adapter = new DynamicAdapter(getContext());
            rv.setAdapter(adapter);
        }
        else{
            Log.i(TAG, "THIS IS GETTING SET UP LIKE I WANT IT");
            adapter2 = new MovieListAdapter();
            rv.setAdapter(adapter2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lists, container, false);
        Log.i(TAG, "==============================");
        setUpRecyclerView(v);

        if (list != null) {
            Log.i(TAG, list);
            DatabaseReference firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
            Query watch = firebaseDatabaseReference.child("lists").child(list).orderByValue();
            if (watch == null)
                throw new IllegalStateException("This should not be null");
            watch.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildAdded: " + dataSnapshot.getKey() + ":" + dataSnapshot.getValue());

                    if (!adapter.contains(dataSnapshot.getKey())) {
                        adapter.addItem(new MovieRecord(dataSnapshot.getKey(), dataSnapshot.getValue().toString()));
                        adapter.notifyDataSetChanged();
                    }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildChanged: " + dataSnapshot.getKey() + ":" + dataSnapshot.getValue());

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved: " + dataSnapshot.getKey() + ":" + dataSnapshot.getValue());
                    adapter.removeItem(dataSnapshot.getKey());

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    Log.d(TAG, "onChildMoved: : " + dataSnapshot.getKey() + ":" + dataSnapshot.getValue());


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        if(movieList != null){
            Log.i(TAG, movieList.toString());
            adapter2.setItems(movieList);
            adapter2.notifyDataSetChanged();
        }


        return v;
    }
}

//    // TODO: Rename method, update argument and hook method into UI event

