package cai288.cs371m.project.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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

import java.util.ArrayList;

import cai288.cs371m.project.R;
import cai288.cs371m.project.customClasses.DynamicAdapter;
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
    private String list;
    private ArrayList<MovieRecord> mList;
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

    public static ListsFragment newInstance(ArrayList<MovieRecord> movieList) {
        ListsFragment fragment = new ListsFragment();
        Bundle args = new Bundle();
        args.putSerializable("mList", movieList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = getArguments() != null ? getArguments().getString("list") : null;
        mList = getArguments() != null ? (ArrayList<MovieRecord>) getArguments().getSerializable("mList") : null;
    }

    private void setUpRecyclerView(View v) {
        RecyclerView rv = (RecyclerView) v.findViewById(R.id.fragmentRecyclerView);
        LinearLayoutManager rv_layout_mgr = new GridLayoutManager(getContext(), 3);
        rv.setLayoutManager(rv_layout_mgr);
        rv.setItemAnimator(new DefaultItemAnimator());

        adapter = new DynamicAdapter(true);
        rv.setAdapter(adapter);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lists, container, false);
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
                    MovieRecord m = new MovieRecord(dataSnapshot.getKey(), dataSnapshot.getValue().toString());
                    if (!adapter.contains(m)) {
                        adapter.addItem(m);
                    }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildChanged: " + dataSnapshot.getKey() + ":" + dataSnapshot.getValue());

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved: " + dataSnapshot.getKey() + ":" + dataSnapshot.getValue());
                    MovieRecord m = new MovieRecord(dataSnapshot.getKey(), dataSnapshot.getValue().toString());
                    adapter.removeItem(m);

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
        if(mList != null){
            adapter.setItems(mList);
        }


        return v;
    }
}

//    // TODO: Rename method, update argument and hook method into UI event

