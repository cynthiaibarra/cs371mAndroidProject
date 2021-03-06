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

import org.apache.commons.lang3.text.WordUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import cai288.cs371m.project.R;

/**
 * Created by Cynthia on 11/11/2016.
 */

public class DatabaseManager {
    public static final int WATCHLIST = 0;
    public static final int FAVELIST = 1;
    public interface getFriendsListener {
        void friendAddedCallback(String friend);

        void friendRemovedCallback(String friend);

    }

    public interface getMoviesListener {
        void getMoviesList(ArrayList<MovieRecord> movies);

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
    public static void deleteFriend(final String userEmail, final String friendEmail){
        db.child("user").child(userEmail.replace(".", "_")).child("friends").orderByValue()
                .equalTo(friendEmail.replace(".","_"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChildren()){
                            for(DataSnapshot child: dataSnapshot.getChildren()){
                                Query q = db.child("user").child(userEmail.replace(".", "_")).child("friends")
                                        .child(child.getKey());
                                q.getRef().setValue(null);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        db.child("user").child(friendEmail.replace(".", "_")).child("friends").orderByValue()
                .equalTo(userEmail.replace(".","_"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                Query l = db.child("user").child(friendEmail.replace(".", "_")).child("friends")
                                        .child(child.getKey());
                                l.getRef().setValue(null);
                            }
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
        db.child("user").child(friendEmail.replace(".", "_")).child("friends").push().setValue(userEmail.replace(".","_"));
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
    public static void getUserByName(final String name, final getUserListener listener){
        Query q = db.child("user").orderByChild("name").equalTo(WordUtils.capitalizeFully(name));
        getAppUser(q, listener);

    }


    public static void getUser(final String userEmail, final getUserListener listener){
        Query q = db.child("user").child(userEmail.toLowerCase());
        getAppUser(q, listener);
    }

    private static void getAppUser(Query q, final getUserListener listener){
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

    public static void getCommonMoviesList(int type, String userEmail,
                                           String friendsEmail, final getMoviesListener listener){
        String uEmail = userEmail.replace(".","_");
        String fEmail = friendsEmail.replace(".", "_");
        String userList = "", friendList = "";
        final Query uWatch, fWatch;
        if (type == R.string.watch_list){
            userList = uEmail + "_watchList";
            friendList = fEmail + "_watchList";

        } else {
            userList = uEmail + "_favoriteList";
            friendList = fEmail + "_favoriteList";

        }
        uWatch = db.child("lists").child(userList).orderByValue();
        fWatch = db.child("lists").child(friendList).orderByValue();
        Log.i(TAG, userList);
        Log.i(TAG, friendList);
        uWatch.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Log.i(TAG, (String) "uWatch");
                fWatch.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot fDataSnapshot) {
                        Log.i(TAG, "uWatch");
                        for(DataSnapshot d: fDataSnapshot.getChildren()){
                            Log.i(TAG, (String) d.getValue());
                        }
                        HashMap<String, String> data = (HashMap<String, String>) fDataSnapshot.getValue();
                        HashMap<String, String> fMovies = new HashMap<String, String>();
                        ArrayList<MovieRecord> commonMovies = new ArrayList<>();
                        if(data != null && fMovies != null){
                            for(String key: data.keySet()){
                                fMovies.put(data.get(key), key);
                            }
                            for(DataSnapshot movie: dataSnapshot.getChildren()){
                                Log.i(TAG, (String) movie.getValue());
                                if(fMovies.containsKey((String) movie.getValue())){
                                    commonMovies.add(new MovieRecord((String) movie.getKey(), (String) movie.getValue()));
                                }

                            }

                        }





                        listener.getMoviesList(commonMovies);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*getString(R.string.favorite_list)*/
    }
}
