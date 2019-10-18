package com.example.myapplication.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.SharedAdapter;
import com.example.myapplication.model.Exercise;
import com.example.myapplication.model.Workout;
import com.example.myapplication.utils.MyConstant;
import com.example.myapplication.utils.NetworkChangeReceiver;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PostsActivity extends AppCompatActivity {
    private static final String TAG = "PostsActivity";
    public NetworkChangeReceiver receiver;
    Boolean bl = true;
    @BindView(R.id.notFoundTextView)
    TextView notFoundTextView;
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.bgImageView)
    ImageView bgImageView;
    @BindView(R.id.loadingTextView)
    TextView loadingTextView;
    @BindView(R.id.loadingProgressbar)
    ProgressBar loadingProgressbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private List<String> followingIdList = new ArrayList<>();
    private List<Exercise> exerciseList = new ArrayList<>();
    private List<Workout> workoutList = new ArrayList<>();
    private List<String> dateList = new ArrayList<>();
    private SharedAdapter sharedAdapter;

    @OnClick(R.id.button)
    void openFindUsers() {
        Intent i = new Intent(PostsActivity.this, FindUsersActivity.class);
        i.putExtra("source", "find");
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        ButterKnife.bind(this);


        /**setting up custom toolbar*/
        setSupportActionBar(toolbar);


        getFollowingUid();
        checkInternet();

    }

    private void getFollowingUid() {
        final DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("users").child(MyConstant.loggedInUserId);
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingIdList.clear();
                if (dataSnapshot.hasChild("followingUID")) {
                    users.child("followingUID").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                followingIdList.add(ds.getValue().toString());

                            }
                            getExercises();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                } else {
                    //loged in user has no people he follows
                    //so no workouts or exercises to show
                    notFoundTextView.setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);
                    bgImageView.setVisibility(View.VISIBLE);
                    //hide loading
                    loadingTextView.setVisibility(View.GONE);
                    loadingProgressbar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getExercises() {


        dateList.clear();
        //get exercises
        exerciseList.clear();
        final Exercise exercise = new Exercise();
        DatabaseReference exerciseNode = FirebaseDatabase.getInstance().getReference("excercises");
        exerciseNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsBig : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds : dsBig.getChildren()) {
                        for (int i = 0; i < followingIdList.size(); i++) {

                            if (ds.child("creatorId").getValue().equals(followingIdList.get(i))) {
                                exercise.setExecution(ds.child("execution").getValue().toString());
                                exercise.setName(ds.child("name").getValue().toString());
                                if (ds.hasChild("additional_notes"))
                                    exercise.setAdditional_notes(ds.child("additional_notes").getValue().toString());
                                exercise.setMechanism(ds.child("mechanism").getValue().toString());
                                exercise.setPreviewPhoto1(ds.child("previewPhoto1").getValue().toString());
                                exercise.setPreviewPhoto2(ds.child("previewPhoto2").getValue().toString());
                                exercise.setDate(ds.child("date").getValue().toString());
                                exercise.setCreatorId(ds.child("creatorId").getValue().toString());

                                dateList.add(ds.child("date").getValue().toString());
                                exerciseList.add(exercise);

                            }
                        }
                    }
                }
                getWorkouts();


                //

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    void getWorkouts() {


        Log.i(TAG, "getWorkouts: ");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("workout").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChangegetWorkouts: ");
                workoutList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //only show in main list the workouts that admin added
                    for (int i = 0; i < followingIdList.size(); i++) {

                        if (ds.child("creatorId").getValue().equals(followingIdList.get(i))) {
                            Workout workout = new Workout();
                            workout.setName(ds.child("name").getValue().toString());
                            workout.setDuration(ds.child("duration").getValue().toString() + " mins");
                            workout.setExercisesNumber(ds.child("exercisesNumber").getValue().toString());
                            workout.setPhotoLink(ds.child("photoLink").getValue().toString());
                            workout.setId(ds.child("id").getValue().toString());
                            workout.setDate(ds.child("date").getValue().toString());
                            workout.setCreatorId(ds.child("creatorId").getValue().toString());

                            dateList.add(ds.child("date").getValue().toString());
                            workoutList.add(workout);
                        }

                    }
                }

                setupRecycler();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setupRecycler() {


        //hide loading
        loadingTextView.setVisibility(View.GONE);
        loadingProgressbar.setVisibility(View.GONE);

        Log.i(TAG, "setupRecycler: " + dateList.size());
        if (dateList.size() == 0) {
            notFoundTextView.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
            bgImageView.setVisibility(View.VISIBLE);
            return;
        }
        //hide the view
        notFoundTextView.setVisibility(View.INVISIBLE);
        button.setVisibility(View.INVISIBLE);
        bgImageView.setVisibility(View.INVISIBLE);

        //init recycler

        Collections.sort(dateList);

        RecyclerView recyclerView = findViewById(R.id.sharedRv);
        sharedAdapter = new SharedAdapter(PostsActivity.this, dateList, exerciseList, workoutList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostsActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(sharedAdapter);


    }


    public void checkInternet() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver(this);
        registerReceiver(receiver, filter);
        bl = receiver.is_connected();
        Log.d("Boolean ", bl.toString());
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {

        }
    }


    /**
     * handle back press from toolbar
     */
    @OnClick(R.id.backArrowImageView)
    public void onViewClicked() {
        onBackPressed();
    }


}
