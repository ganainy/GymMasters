package com.example.myapplication.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.VideoPlayerConfig;
import com.example.myapplication.model.User;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    SimpleExoPlayer player;
    private static final String TAG = "MainActivity";
    private CircleImageView imageView;
    FirebaseAuth mAuth;
    private String profilePictureId;
    TextView nameNavigation,emailNavigation;
    private String name,email,rating;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);



        showUserDataInNavigationMenu();

        showGifFromStorage();
    }

    private void showGifFromStorage() {
        storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference pathReference = storageRef.child("ExRx.net - Machine-assisted Chest Dip.MP4");
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i(TAG, "onSuccess: video");
                //download image with glide then show it in the navigation menu
              //  ImageView imageView100=findViewById(R.id.imageView100);
             //   Glide.with(getApplicationContext()).load(uri.toString()).into(imageView100);


                //
                PlayerView videoFullScreenPlayer=findViewById(R.id.videoFullScreenPlayer);
                if (player == null) {
                    // 1. Create a default TrackSelector
                    LoadControl loadControl = new DefaultLoadControl(
                            new DefaultAllocator(true, 16),
                            VideoPlayerConfig.MIN_BUFFER_DURATION,
                            VideoPlayerConfig.MAX_BUFFER_DURATION,
                            VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
                            VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER, -1, true);

                    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                    TrackSelection.Factory videoTrackSelectionFactory =
                            new AdaptiveTrackSelection.Factory(bandwidthMeter);
                    TrackSelector trackSelector =
                            new DefaultTrackSelector(videoTrackSelectionFactory);
                    // 2. Create the player
                    player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(MainActivity.this), trackSelector, loadControl);
                    videoFullScreenPlayer.setPlayer(player);}


                // Measures bandwidth during playback. Can be null if not required.
                DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                // Produces DataSource instances through which media data is loaded.
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(MainActivity.this,
                        Util.getUserAgent(MainActivity.this, getString(R.string.app_name)), bandwidthMeter);
                // This is the MediaSource representing the media to be played.
                MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);
                // Prepare the player with the source.
                player.prepare(videoSource);
                player.setPlayWhenReady(true);

                player.addListener(new Player.EventListener() {
                    @Override
                    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

                    }

                    @Override
                    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                    }

                    @Override
                    public void onLoadingChanged(boolean isLoading) {

                    }

                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                    }

                    @Override
                    public void onRepeatModeChanged(int repeatMode) {

                    }

                    @Override
                    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                    }

                    @Override
                    public void onPlayerError(ExoPlaybackException error) {
                        Log.d(TAG, "onPlayerError: "+error.getSourceException());
                    }

                    @Override
                    public void onPositionDiscontinuity(int reason) {

                    }

                    @Override
                    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                    }

                    @Override
                    public void onSeekProcessed() {

                    }
                });


                //
                Log.i(TAG, "onSuccess: gif");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.i(TAG, "onFailure gif: "+exception.getMessage());
            }
        });


    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab")).
                createMediaSource(uri);
    }
    private void showUserDataInNavigationMenu() {
        //used callback so we only try to show the image  after the id is retreived from the database otherwise it would be null
        getUserData(new FirebaseCallback() {
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
                    StorageReference pathReference = storageRef.child("images/"+profilePictureId);

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

    private void getUserData(final FirebaseCallback firebaseCallback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        mAuth = FirebaseAuth.getInstance();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                profilePictureId=dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("photo").getValue().toString();
                name=dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("name").getValue().toString();
                email=dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("email").getValue().toString();
                rating=dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("rating").getValue().toString();


                firebaseCallback.onCallback(new User(name,email,rating,profilePictureId));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private interface FirebaseCallback{
        void onCallback(User loggedInUser);
    }
}
