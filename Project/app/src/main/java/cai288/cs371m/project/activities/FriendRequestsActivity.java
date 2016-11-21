package cai288.cs371m.project.activities;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;

import cai288.cs371m.project.R;
import cai288.cs371m.project.customClasses.AppUser;
import cai288.cs371m.project.customClasses.DatabaseManager;
import cai288.cs371m.project.customClasses.FriendAdapter;

public class FriendRequestsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);
        Toolbar tb = (Toolbar) findViewById(R.id.friendRequestToolbar);
        setSupportActionBar(tb);
        ImageButton back = (ImageButton) findViewById(R.id.movieinfo_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final FriendAdapter adapter1 = new FriendAdapter(FriendAdapter.TYPE_FRIEND_REQUEST_RECEIVED);
        final FriendAdapter adapter2 = new FriendAdapter(FriendAdapter.TYPE_FRIEND_REQUEST_SENT);
        RecyclerView rv1 = (RecyclerView) findViewById(R.id.friend_requests_received_list);
        RecyclerView rv2 = (RecyclerView) findViewById(R.id.friend_requests_sent_list);
        LinearLayoutManager rv_layout_mgr = new LinearLayoutManager(this);
        LinearLayoutManager rv_layout_mgr2 = new LinearLayoutManager(this);
        rv1.setLayoutManager(rv_layout_mgr);
        rv1.setItemAnimator(new DefaultItemAnimator());
        rv1.setAdapter(adapter1);
        rv2.setLayoutManager(rv_layout_mgr2);
        rv2.setItemAnimator(new DefaultItemAnimator());
        rv2.setAdapter(adapter2);

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DatabaseManager.getReceivedRequestsList(email, new DatabaseManager.getFriendsListener() {
            @Override
            public void friendAddedCallback(String friend) {
                if(!adapter1.contains(friend))
                    DatabaseManager.getUser(friend, new DatabaseManager.getUserListener() {
                        @Override
                        public void getUserCallback(AppUser user) {
                            adapter1.addItem(user);
                        }
                    });
            }

            @Override
            public void friendRemovedCallback(String friend) {
                adapter1.removeItem(friend);

            }
        });

        DatabaseManager.getSentRequestsList(email, new DatabaseManager.getFriendsListener() {
            @Override
            public void friendAddedCallback(String friend) {
                if(!adapter2.contains(friend))
                    DatabaseManager.getUser(friend, new DatabaseManager.getUserListener() {
                        @Override
                        public void getUserCallback(AppUser user) {
                            adapter2.addItem(user);
                        }
                    });
            }

            @Override
            public void friendRemovedCallback(String friend) {
                adapter2.removeItem(friend);

            }
        });



    }
}
