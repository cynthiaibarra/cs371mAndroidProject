package cai288.cs371m.project.customClasses;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import cai288.cs371m.project.R;
import cai288.cs371m.project.activities.MainActivity;
import cai288.cs371m.project.activities.MovieInfoActivity;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Cynthia on 11/5/2016.
 */

public class FriendAdapter extends GenericAdapter<AppUser> implements DatabaseManager.getFriendsListener{
    private int type = 500;
    public static final int TYPE_FRIEND = 0;
    public static final int TYPE_ADD_FRIEND = 1;
    public static final int TYPE_FRIEND_REQUEST_RECEIVED = 2;
    public static final int TYPE_FRIEND_REQUEST_SENT = 3;

    private ArrayList<String> friendsList = new ArrayList<>();
    private static final String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();


    public FriendAdapter(int type){
        super();
        this.type = type;
        //DatabaseManager.getFriendsList(userEmail, this);
    }

    public FriendAdapter(){
        super();
    }

    @Override
    public boolean contains(AppUser user){
        for(AppUser u: list){
            if (u.getEmail().equals(user.getEmail()))
                return true;
        }
        return false;
    }

    public boolean contains(String friend){
        friend = friend.replace("_",".");
        for(AppUser u: list){
            if (u.getEmail().equals(friend))
                return true;
        }
        return false;
    }

    @Override
    public void friendAddedCallback(String friend) {
        friendsList.add(friend.replace("_", "."));
    }

    @Override
    public void friendRemovedCallback(String friend) {
        friendsList.remove(friend.replace("_", "."));

    }


    private class FriendViewHolder extends RecyclerViewHolder{
        public TextView name;
        public CircleImageView profilePic;
        public TextView email;
        public Button addFriendBtn;
        public View container;
        public TextView friendRequestSent;

        public FriendViewHolder(View itemView){
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.friend_row_name);
            this.profilePic = (CircleImageView) itemView.findViewById(R.id.friend_row_profilePic);
            this.email = (TextView) itemView.findViewById(R.id.friend_row_email);
            this.addFriendBtn = (Button) itemView.findViewById(R.id.friend_row_addBtn);
            addFriendBtn.setVisibility(View.GONE);
            this.container = itemView;
            this.friendRequestSent = (TextView) itemView.findViewById(R.id.friend_row_request_sent);
            friendRequestSent.setVisibility(View.GONE);


        }

        @Override
        public void onClick(View v) {
        }
    }

    public void removeItem(String useremail){
        String email = useremail.replace("_", ".");
        int index = -1;
        int i = 0;
        while(i < getItemCount()){
            String e = getItem(i).getEmail();
            if (e.equals(email))
                index = i;
            i++;
        }
        if (index > 0)
            removeItem(index);

    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_row_layout, parent, false);
        return new FriendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GenericAdapter.RecyclerViewHolder holder, int position) {
        final AppUser user = getItem(position);
        if(user.friends != null){
            Log.i("meep", user.friends.toString());
        }
        final FriendViewHolder h = (FriendViewHolder) holder;
        RelationshipStatus relationship = determineRelationship(user);
        h.name.setText(user.getName());
        h.email.setText(user.getEmail());
        new GetImage(h.profilePic).execute(user.getPhoto());
        Log.i("determineRelationship", "" + relationship.friends);

        if(type == TYPE_ADD_FRIEND && !relationship.friends && !relationship.requested){
            if(friendsList.indexOf(user.getEmail()) < 0){
                h.friendRequestSent.setVisibility(View.GONE);
                h.addFriendBtn.setVisibility(View.VISIBLE);
                h.addFriendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseManager.requestFriend(userEmail, user.getEmail());
                       // friendsList.add(user.getEmail());
                        v.setVisibility(View.GONE);
                        h.friendRequestSent.setVisibility(View.VISIBLE);
                    }
                });
            }

        }else if(type == TYPE_ADD_FRIEND && relationship.requested){
            h.friendRequestSent.setVisibility(View.VISIBLE);
            h.addFriendBtn.setVisibility(View.GONE);
        }else if(type == TYPE_ADD_FRIEND && relationship.friends){
            h.friendRequestSent.setText("Friends");
            h.addFriendBtn.setVisibility(View.GONE);
            h.friendRequestSent.setVisibility(View.VISIBLE);

        }else if(type == TYPE_FRIEND_REQUEST_SENT){
            h.friendRequestSent.setText("Request Pending");
            h.friendRequestSent.setVisibility(View.VISIBLE);
        }else if(type == TYPE_FRIEND_REQUEST_RECEIVED){
            h.addFriendBtn.setVisibility(View.VISIBLE);
            h.addFriendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setVisibility(View.GONE);
                    h.friendRequestSent.setText("Friend Added!");
                    h.friendRequestSent.setVisibility(View.VISIBLE);
                    DatabaseManager.addFriend(userEmail, user.getEmail());
                    DatabaseManager.friendRequestAccepted(userEmail, user.getEmail());

                }
            });
        }

    }

    private RelationshipStatus determineRelationship(AppUser user) {
        RelationshipStatus relationship = new RelationshipStatus();
        String uEmail = userEmail.replace(".", "_");
        if(user.friends != null)
            relationship.friends = user.friends.contains(uEmail);
        else
            relationship.friends = false;
        if(user.receivedRequests != null)
            relationship.requested = user.receivedRequests.contains(userEmail.replace(".", "_"));
        else
            relationship.requested = false;
        return relationship;
    }

    private class GetImage extends AsyncTask<String, Void, Bitmap> {

        private CircleImageView profilePic;
        public GetImage(CircleImageView circleImageView){
            this.profilePic = circleImageView;
        }

        protected Bitmap doInBackground(String ... params) {
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            Bitmap image = null;
            URL searchURL = null;
            try{
                searchURL = new URL(params[0]);
            }catch (Exception e){
                Log.e("FriendAdapter", "get image url creation failed");
            }
            if(searchURL != null) {
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
            }
            return image;
        }
        protected void onPostExecute(Bitmap b) {
            Log.i("here", "++++++++");
            if(profilePic != null){
                if(b != null)
                    profilePic.setImageBitmap(b);
                else
                    profilePic.setImageResource(R.drawable.ic_account_circle_black_36dp);
            }
        }
    }

    private class RelationshipStatus{
        private boolean friends;
        private boolean requested;
    }
}