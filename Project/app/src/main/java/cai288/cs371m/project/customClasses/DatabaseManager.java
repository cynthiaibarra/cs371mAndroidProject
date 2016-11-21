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

import java.lang.reflect.Array;
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

    public static void friendRequestAccepted(final String userEmail, final String friendEmail) {
        String uEmail = userEmail.replace(".", "_");
        final String fEmail = friendEmail.replace(".", "_");
        db.child("user").child(uEmail).child("receivedRequests").orderByValue().equalTo(fEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().setValue(null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        db.child("user").child(fEmail).child("sentRequests").orderByValue().equalTo(uEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().setValue(null);
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



    public static void requestFriend(final String userEmail, final String friendEmail) {
        if (db == null) {
            Log.d(TAG, "db == null");
            return;
        }
        String uEmail = userEmail.replace(".", "_");
        String fEmail = friendEmail.replace(".", "_");
        db.child("user").child(uEmail).child("sentRequests").push().setValue(fEmail);
        db.child("user").child(fEmail).child("receivedRequests").push().setValue(uEmail);
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
                listener.friendRemovedCallback(friend);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void getSentRequestsList(final String userEmail, final getFriendsListener listener) {
        if (db == null) {
            Log.d(TAG, "db == null");
            return;
        }
        String e = userEmail.replace(".","_");
        Query q = db.child("user").child(e).child("sentRequests").orderByValue();
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
                listener.friendRemovedCallback(friend);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void getReceivedRequestsList(final String userEmail, final getFriendsListener listener) {
        if (db == null) {
            Log.d(TAG, "db == null");
            return;
        }
        String e = userEmail.replace(".","_");
        Query q = db.child("user").child(e).child("receivedRequests").orderByValue();
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
                listener.friendRemovedCallback(friend);

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
                            AppUser user = null;
                            if(dataSnapshot.getChildrenCount() > 0) {
                                HashMap<String, Object> userMap = (HashMap<String, Object>) dataSnapshot.getValue();

                                user = new AppUser((String) userMap.get("email"), (String) userMap.get("name"),
                                        (String) userMap.get("photo"), (String) userMap.get("uid"));
                                Log.d(TAG, "db == friends");
                                if (userMap.get("friends") != null) {
                                    Log.d(TAG, "db == friends!!!!!!!!!!!");
                                    HashMap<String, String> friends = (HashMap<String, String>) userMap.get("friends");
                                    user.friends = new ArrayList<>();
                                    for (String key : friends.keySet()) {
                                        user.friends.add(friends.get(key));
                                    }

                                }
                                if (userMap.get("sentRequests") != null) {
                                    HashMap<String, String> requests = (HashMap<String, String>) userMap.get("sentRequests");
                                    user.sentRequests = new ArrayList<>();
                                    for (String key : requests.keySet()) {
                                        user.sentRequests.add(requests.get(key));
                                    }
                                }
                                if (userMap.get("receivedRequests") != null) {
                                    HashMap<String, String> requests = (HashMap<String, String>) userMap.get("receivedRequests");
                                    user.receivedRequests = new ArrayList<>();
                                    for (String key : requests.keySet()) {
                                        user.receivedRequests.add(requests.get(key));
                                    }
                                }
                                if (user == null) {
                                    Log.d(TAG, "user == null");
                                }
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
