package ganainy.dev.gymmasters.ui.posts;

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

import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.SharedExerciseWorkout;
import ganainy.dev.gymmasters.models.app_models.Workout;
import ganainy.dev.gymmasters.ui.findUser.FindUsersActivity;
import ganainy.dev.gymmasters.utils.MyConstant;
import ganainy.dev.gymmasters.utils.NetworkChangeReceiver;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private List<SharedExerciseWorkout> sharedExerciseWorkoutList = new ArrayList<>();
    private SharedAdapter sharedAdapter;
    private RecyclerView recyclerView;

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

        recyclerView = findViewById(R.id.sharedRv);


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



        DatabaseReference exerciseNode = FirebaseDatabase.getInstance().getReference("excercises");
        exerciseNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsBig : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds : dsBig.getChildren()) {
                        for (int i = 0; i < followingIdList.size(); i++) {
                            if (ds.child("creatorId").getValue().equals(followingIdList.get(i))) {
                                Exercise exercise = new Exercise();
                                exercise.setExecution(ds.child("execution").getValue().toString());
                                exercise.setName(ds.child("name").getValue().toString());
                                if (ds.hasChild("additional_notes"))
                                    exercise.setAdditional_notes(ds.child("additional_notes").getValue().toString());
                                exercise.setMechanism(ds.child("mechanism").getValue().toString());
                                exercise.setPreviewPhoto1(ds.child("previewPhoto1").getValue().toString());
                                exercise.setPreviewPhoto2(ds.child("previewPhoto2").getValue().toString());
                                exercise.setDate(ds.child("date").getValue().toString());
                                exercise.setCreatorId(ds.child("creatorId").getValue().toString());


                                sharedExerciseWorkoutList.add(new SharedExerciseWorkout(exercise, 0, Long.valueOf(exercise.getDate())));

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
                            workout.setLevel(ds.child("level").getValue().toString());


                            sharedExerciseWorkoutList.add(new SharedExerciseWorkout(workout, 1, Long.valueOf(workout.getDate())));
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


        if (sharedExerciseWorkoutList.size() == 0) {
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


        Collections.sort(sharedExerciseWorkoutList, new Comparator<SharedExerciseWorkout>() {

            @Override
            public int compare(SharedExerciseWorkout s1, SharedExerciseWorkout s2) {
                return s2.getDateStamp().compareTo(s1.getDateStamp());
            }
        });


       /* for (int i = 0; i < sharedExerciseWorkoutList.size(); i++) {
            Log.i(TAG, "sharedExerciseWorkoutList: "+sharedExerciseWorkoutList.get(i).getDateStamp()+"----entity"+sharedExerciseWorkoutList.get(i)
            .getEntityType());
        }*/


        sharedAdapter = new SharedAdapter(PostsActivity.this, sharedExerciseWorkoutList);
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

        Log.i(TAG, "onPause: ");
        recyclerView.setAdapter(null);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        if (sharedAdapter != null) recyclerView.setAdapter(sharedAdapter);
    }

    /**
     * handle back press from toolbar
     */
    @OnClick(R.id.backArrowImageView)
    public void onViewClicked() {
        onBackPressed();
    }


}
