package cai288.cs371m.project.customClasses;

import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Cynthia on 11/11/2016.
 */

public class DatabaseManager {
    public interface getFriendsListener {
        void friendAddedCallback(String friend);

        void friendRemovedCallback(String friend);

    }

    public interface getUserListener {
        void getUserCallback(AppUser user);
    }

    public interface ContainsUserListener {
        void containsUserCallback(String user, boolean result);
    }
//    public interface searchByDateListener {
//        void searchByDateCallback(long beginDate, long endDate, List<PhotoObject> photos);
//    }

    private static DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private static String TAG = "DatabaseManager: ";

    public DatabaseManager() {

    }

    public static void containsUser(final String userEmail, final ContainsUserListener listener){
        String email = userEmail.replace(".", "_");
        Query q = db.child("user").child(email);
        final boolean result = false;
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    listener.containsUserCallback(userEmail, true);
                } else {
                    listener.containsUserCallback(userEmail, false);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    public static void addFriend(final String userEmail, final String friendEmail) {
        if (db == null) {
            Log.d(TAG, "db == null");
            return;
        }
        db.child("user").child(userEmail.replace(".", "_")).child("friends").push().setValue(friendEmail.replace(".","_"));
    }

    public static void addFriendRequest(final String userEmail, final String friendEmail) {
        if (db == null) {
            Log.d(TAG, "db == null");
            return;
        }
        db.child("user").child(userEmail).child("fRequests").push().setValue(friendEmail);
    }

    public static void getFriendsList(final String userEmail, final getFriendsListener listener) {
        if (db == null) {
            Log.d(TAG, "db == null");
            return;
        }
        String e = userEmail.replace(".","_");
        Query q = db.child("user").child(e).child("friends").orderByValue();
        q.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "CHILD ADDED");
                String friend = dataSnapshot.getValue().toString();
                listener.friendAddedCallback(friend);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "WHY IS THS GETTING REMOVED");
                String friend = dataSnapshot.getValue().toString();
                //listener.friendRemovedCallback(friend);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    public static void getUser(final String userEmail, final getUserListener listener){
        if (db == null) {
            Log.d(TAG, "db == null");
            return;
        }
        Log.d(TAG, "db == hereee");
        Query q = db.child("user").child(userEmail);
        if(q != null){
            q.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String, String> userMap = (HashMap<String, String>) dataSnapshot.getValue();
                            if(userMap == null)
                                listener.getUserCallback(null);
                            AppUser user = new AppUser(userMap.get("email"), userMap.get("name"),
                                    userMap.get("photo"), userMap.get("uid"));
                            if(user == null){
                                Log.d(TAG, "user == null");
                            }
                            listener.getUserCallback(user);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    }
            );
        }

    }
}
