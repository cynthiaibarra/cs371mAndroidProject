package cai288.cs371m.project.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import cai288.cs371m.project.customClasses.AppUser;
import cai288.cs371m.project.R;
import cai288.cs371m.project.ViewPagerAdapter;
import cai288.cs371m.project.customClasses.MovieRecord;
import de.hdodenhof.circleimageview.CircleImageView;
import info.movito.themoviedbapi.model.MovieDb;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private final String TAG = "MainActivity: ";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFireBaseUser;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference firebaseDatabaseReference;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView headerName;
    private TextView headerUsername;
    private CircleImageView headerProfilePic;
    private String email;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initHeaderVariables(toolbar);
        authenticate();
        initViewPager();
        initNavigationViewMenu();
    }

    private void authenticate() {
        googleApiClient =  new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFireBaseUser = mFirebaseAuth.getCurrentUser();
        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        if (mFireBaseUser == null){
            login();
        }else{
            String uid = mFireBaseUser.getUid();
            email = mFireBaseUser.getEmail().replace(".", "_");
            AppUser user = new AppUser(mFireBaseUser.getEmail(), mFireBaseUser.getDisplayName(),
                    mFireBaseUser.getPhotoUrl().toString(), mFireBaseUser.getUid());
            firebaseDatabaseReference.child("user").child(email).setValue(user);
        }

        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                if (firebaseAuth == null){
                    return;
                }
                if(mFireBaseUser.getDisplayName() == null || mFireBaseUser.getEmail() == null){
                    login();
                } else {
                    headerName.setText(mFireBaseUser.getDisplayName());
                    headerUsername.setText(mFireBaseUser.getEmail());
                    URL profilePic = null;
                    try{
                        profilePic = new URL(mFireBaseUser.getPhotoUrl().toString());
                    } catch (Exception e){
                        Log.e(TAG, e.toString());
                    }
                    new GetImage().execute(profilePic);

                }
            }
        };
    }

    private void login(){
        startActivity(new Intent(this, SignInActivity.class));
        finish();
        return;
    }

    private void initHeaderVariables(Toolbar toolbar) {
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        View layout = navigationView.getHeaderView(0);
        headerName = (TextView) layout.findViewById(R.id.header_name);
        headerUsername = (TextView) layout.findViewById(R.id.header_username);
        headerProfilePic = (CircleImageView) layout.findViewById(R.id.header_profilePic);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open, R.string.close){
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }

    public void initViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabLayout tabLayout = (TabLayout)  findViewById(R.id.tabLayout);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        String watchList = email + getString(R.string.watch_list);
        String faveList = email + getString(R.string.favorite_list);


        adapter.addFragment(new ListFragment(), "SOCIAL");
        adapter.addFragment(ListsFragment.newInstance(watchList), "WATCHLIST");
        adapter.addFragment(ListsFragment.newInstance(faveList), "FAVELIST");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initNavigationViewMenu() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( MenuItem item) {
                item.setChecked(false);
                drawerLayout.closeDrawers();
                switch (item.getItemId()){
                    case R.id.signout:
                        mFirebaseAuth.signOut();
                        Auth.GoogleSignInApi.signOut(googleApiClient);
                        login();
                        break;
                    case R.id.nav_searchFriends:
                        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                        intent.putExtra("searchType", SearchActivity.SEARCH_FRIENDS);
                        startActivity(intent);
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

//    @Override
//    public void onResume(){
//        super.onResume();
//        mFirebaseAuth.addAuthStateListener(mAuthListener);
//    }
//
//    @Override
//    public void onPause(){
//        super.onPause();
//        mFirebaseAuth.addAuthStateListener(null);
//    }
    @Override
    public void onStop(){
        super.onStop();
        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }


    //TODO: finish inflating menu so search appears
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
//            case R.id.sign_out_menu:
//                mFirebaseAuth.signOut();
//                Auth.GoogleSignInApi.signOut(googleApiClient);
//                startActivity(new Intent(this, SignInActivity.class));
//                return true;
            case R.id.search:
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateProfilePic(Bitmap b) {
        headerProfilePic.setImageBitmap(b);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private class GetImage extends AsyncTask<URL, Void, Bitmap> {
        protected Bitmap doInBackground(URL... param) {
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            Bitmap image = null;
            URL searchURL = param[0];

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
            return image;
        }
        protected void onPostExecute(Bitmap b) {
            updateProfilePic(b);
        }
    }


}
