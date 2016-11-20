package cai288.cs371m.project.activities;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;

import cai288.cs371m.project.R;
import cai288.cs371m.project.customClasses.AppUser;
import cai288.cs371m.project.customClasses.DatabaseManager;
import cai288.cs371m.project.customClasses.DynamicAdapter;
import cai288.cs371m.project.customClasses.FriendAdapter;
import cai288.cs371m.project.customClasses.MovieRecord;

/**

 */
public class FriendsFragment extends Fragment implements DatabaseManager.getFriendsListener,
        DatabaseManager.getUserListener {
    private FriendAdapter adapter;
    private String userEmail;
    private String TAG = "FriendsFragment: ";
    private ArrayList<String> friends = new ArrayList<>();
    private DatabaseManager databaseManager;

    // private OnFragmentInteractionListener mListener;

    public FriendsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String user) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putString("userEmail", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new FriendAdapter(FriendAdapter.TYPE_FRIEND);


    }

    private void setUpRecyclerView(View v) {
        RecyclerView rv = (RecyclerView) v.findViewById(R.id.fragmentRecyclerView);
        LinearLayoutManager rv_layout_mgr = new LinearLayoutManager(getContext());
        rv.setLayoutManager(rv_layout_mgr);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(adapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lists, container, false);
        setUpRecyclerView(v);
        userEmail = getArguments() != null ? getArguments().getString("userEmail") : null;
        if(userEmail != null)
            new DatabaseManager().getFriendsList(userEmail, this);
        return v;
    }

    @Override
    public void friendAddedCallback(String friend) {
        DatabaseManager.getUser(friend, this);
    }

    @Override
    public void friendRemovedCallback(String friend) {
        adapter.removeItem(friend);
    }

    @Override
    public void getUserCallback(AppUser user) {
        if(!adapter.contains(user))
            adapter.addItem(user);
    }


}