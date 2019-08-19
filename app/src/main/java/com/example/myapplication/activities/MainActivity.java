package com.example.myapplication.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.MainActivityViewModel;
import com.example.myapplication.PostsActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapters.ViewPagerAdapterCreateWorkout;
import com.example.myapplication.adapters.ViewPagerAdapterMainActivity;
import com.example.myapplication.model.User;
import com.example.myapplication.model.Workout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SelectedBundle selectedBundle;
    SelectedBundle2 selectedBundle2;
    Workout workout;
    private static final String TAG = "MainActivity";
    private CircleImageView imageView;
    FirebaseAuth mAuth;
    private String profilePictureId;
    TextView nameNavigation,emailNavigation;
    private String name,email,rating;
    private StorageReference storageRef;


    @BindView(R.id.view_pager_main)
    ViewPager view_pager_main;

    @BindView(R.id.view_pager_create_workout)
    ViewPager view_pager_create_workout;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        setupMainViewPager();
        showUserDataInNavigationMenu();

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


                }
                return false;
            }
        });
        {

        }
    }

    private void handleFollowersClick() {
    }

    private void handleFindClick() {
        startActivity(new Intent(MainActivity.this, FindUsersActivity.class));
    }

    private void handlePostsClick() {

        startActivity(new Intent(MainActivity.this, PostsActivity.class));
    }

    private void handleFollowedClick() {

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

    public void setupViewPagerForCreateWorkout() {

        view_pager_create_workout.setVisibility(View.VISIBLE);
        view_pager_main.setVisibility(View.GONE);


        view_pager_create_workout.setAdapter(new ViewPagerAdapterCreateWorkout(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(view_pager_create_workout, true);
        tabLayout.getTabAt(0).setText(("STEP1"));


        tabLayout.getTabAt(1).setText(("STEP2"));



    }


    public void gotoNextTab() {
        tabLayout.getTabAt(1).select();
    }

    private void showUserDataInNavigationMenu() {
        //used callback so we only try to show the image  after the id is retreived from the database otherwise it would be null
        MainActivityViewModel mViewModel;
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        mViewModel.getUserData(new FirebaseCallback() {
            @Override
            public void onCallback(User loggedInUser) {
                NavigationView navigationView = findViewById(R.id.nav_view);
                View hView =  navigationView.getHeaderView(0);
                imageView = hView.findViewById(R.id.imageViewProfile);
                //set name and email in the navigationViews
                nameNavigation=hView.findViewById(R.id.nameNavigation);
                emailNavigation=hView.findViewById(R.id.emailNavigation);

                nameNavigation.setText(loggedInUser.getName());
                emailNavigation.setText(loggedInUser.getEmail());

               //imageUri is not null if we come from signup activity so we can show image directly from uri without downloading again from firebase storage
                if (getIntent().hasExtra("imageUri"))
                {
                    Uri myUri = Uri.parse(getIntent().getStringExtra("imageUri"));
                    Glide.with(MainActivity.this)
                            .load(myUri)
                            .into(imageView);
                }else
                {
                     storageRef = FirebaseStorage.getInstance().getReference();
                    //reference to logged in user profile image

                    StorageReference pathReference = storageRef.child("images/" + loggedInUser.getPhoto());
                    pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //download image with glide then show it in the navigation menu
                            Glide.with(getApplicationContext()).load(uri.toString()).into(imageView);
                            Log.i(TAG, "onSuccess: loaded from storage");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            Log.i(TAG, "onFailure: "+exception.getMessage());
                        }
                    });
                }
            }
        });
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      //sign out when menu item clicked
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signout) {
           FirebaseAuth mAuth=   FirebaseAuth.getInstance();
           mAuth.signOut();
           startActivity(new Intent(MainActivity.this,LoginActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_find) {
            //todo add features of navigation menu items
        } else if (id == R.id.nav_followers) {

        } else if (id == R.id.nav_following) {

        } else if (id == R.id.nav_posts) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void setWorkout(Workout workout)
    {
        this.workout=workout;
        Bundle bundle=new Bundle();
        bundle.putParcelable("workout",workout);
        selectedBundle2.onBundleSelect(bundle);
    }


    //result coming from createWorkoutFragment1
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        if (requestCode == 103) {
            Log.i(TAG, "requestCode: ok");
            //pass image uri to the createworkoutfragment1
            Bundle bundle = new Bundle();
            bundle.putString("imageString", String.valueOf(data.getData()));
            selectedBundle.onBundleSelect(bundle);

        }

    }


    public void setOnBundleSelected(SelectedBundle selectedBundle) {
        this.selectedBundle = selectedBundle;
    }

    public void setOnBundleSelected2(SelectedBundle2 selectedBundle2) {
        this.selectedBundle2 = selectedBundle2;
    }

    public void getPhotoFromGallery() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 103);
    }


    public interface FirebaseCallback{
        void onCallback(User loggedInUser);
    }


    public interface SelectedBundle {
        void onBundleSelect(Bundle bundle);
    }
    public interface SelectedBundle2 {
        void onBundleSelect(Bundle bundle);
    }
}
