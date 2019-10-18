package com.example.myapplication.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.MapsActivity;
import com.example.myapplication.R;
import com.example.myapplication.fragments.ViewPagerAdapterMainActivity;
import com.example.myapplication.utils.NetworkChangeReceiver;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public NetworkChangeReceiver receiver;
    Boolean bl = true;
    SelectedBundle selectedBundle;
    private static final String TAG = "MainActivity";


    @BindView(R.id.view_pager_main)
    ViewPager view_pager_main;


    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        setupMainViewPager();

        //handle click on navigation view items
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_find:
                        handleFindClick();
                        break;
                    case R.id.nav_posts:
                        handlePostsClick();
                        break;
                    case R.id.nav_followers:
                        handleFollowersClick();
                        break;
                    case R.id.nav_following:
                        handleFollowedClick();
                        break;
                    case R.id.nav_timer:
                        handleTimerClick();
                        break;
                    case R.id.nav_map:
                        handleMapClick();
                        break;
                    case R.id.nav_sign_out:
                        handleSignoutClick();
                        break;


                }
                return false;
            }
        });
        {

        }


        checkInternet();
    }

    private void handleMapClick() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
        } else {
            Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            if (response.isPermanentlyDenied()) {
                                //show alert dialog with custom view
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Permission required")
                                        .setMessage("You permanently denied permission ,but it can be changed from settings")
                                        .setIcon(R.drawable.ic_location_on_black_24dp)
                                        .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                i.setData(Uri.fromParts("package", getPackageName(), null));
                                            }
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .show();
                            } else {
                                Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
            /*  //*/

        }
    }

    private void handleSignoutClick() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth != null) {
            mAuth.signOut();
            openLoginActivity();
        } else {
            mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    openLoginActivity();
                }
            });
        }
    }

    private void handleTimerClick() {
        Intent i = new Intent(MainActivity.this, TimerActivity.class);
        startActivity(i);
    }

    private void handleFollowersClick() {
        Intent i = new Intent(MainActivity.this, FindUsersActivity.class);
        i.putExtra("source", "followers");
        startActivity(i);
    }

    private void handleFindClick() {
        Intent i = new Intent(MainActivity.this, FindUsersActivity.class);
        i.putExtra("source", "find");
        startActivity(i);
    }

    private void handlePostsClick() {

        startActivity(new Intent(MainActivity.this, PostsActivity.class));
    }

    private void handleFollowedClick() {
        Intent i = new Intent(MainActivity.this, FindUsersActivity.class);
        i.putExtra("source", "following");
        startActivity(i);
    }

    private void setupMainViewPager() {
        //view pager and tab layout for swiping fragments
        view_pager_main.setAdapter(new ViewPagerAdapterMainActivity(getSupportFragmentManager()));

        tabLayout.setupWithViewPager(view_pager_main, true);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_dumbbell_variant_outline);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_crisscross_position);

        tabLayout.getTabAt(0).setText(("Home"));
        tabLayout.getTabAt(1).setText(("Exercises"));
        tabLayout.getTabAt(2).setText(("Workout"));


    }


    private void openLoginActivity() {
        //finish this activity on log out so users won't be able to log in on back press in login
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_find) {
        } else if (id == R.id.nav_followers) {

        } else if (id == R.id.nav_following) {

        } else if (id == R.id.nav_posts) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //result coming from gallery requested by createWorkoutFragment
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        if (requestCode == 103) {
            Log.i(TAG, "requestCode: ok");
            //pass image uri to the createworkoutfragment
            Bundle bundle = new Bundle();
            bundle.putString("imageString", String.valueOf(data.getData()));
            selectedBundle.onBundleSelect(bundle);

        }

    }


    public void setOnBundleSelected(SelectedBundle selectedBundle) {
        this.selectedBundle = selectedBundle;
    }


    public void getPhotoFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 103);
    }

    @Override
    public void onBackPressed() {


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            /**if there is fragment in back stack close it only*/
            super.onBackPressed();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            // Finish this activity as well as all activities immediately below it
                            finishAffinity();
                        }
                    }).create().show();
        }


    }


    public interface SelectedBundle {
        void onBundleSelect(Bundle bundle);
    }


    @Override
    protected void onPause() {
        super.onPause();

        super.onPause();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {

        }

    }

    public void checkInternet() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver(this);
        registerReceiver(receiver, filter);
        bl = receiver.is_connected();
        Log.d("Boolean ", bl.toString());
    }

}
