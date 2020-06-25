package ganainy.dev.gymmasters.ui.specificWorkout;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.Workout;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ganainy.dev.gymmasters.ui.specificExercise.ExerciseFragment;
import ganainy.dev.gymmasters.ui.specificExercise.youtubeFragment.YoutubeCallback;
import ganainy.dev.gymmasters.ui.specificExercise.youtubeFragment.YoutubeFragment;

public class SpecificWorkoutActivity extends AppCompatActivity implements YoutubeCallback {
    private static final String TAG = "SpecificWorkoutActivity";
    private static final String MY_PREFS_NAME = "mSharedPref";
    @BindView(R.id.specificWorkoutRecycler)
    RecyclerView specificWorkoutRecycler;
    @BindView(R.id.deleteWorkoutFab)
    FloatingActionButton deleteWorkoutFab;
    private List<Exercise> workoutExerciseList;
    private Workout workout;
    private boolean isnotFirstTimeUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //todo check if opened workout is created by logged in user
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_workout);
        ButterKnife.bind(this);


        //get workout info and pass it to downloadExercise method
        workout = getIntent().getParcelableExtra("workout");
        downloadExercises(workout);


        //only show delete fab if workout of logged in user(user coming from main fragment home)
        if (getIntent().hasExtra("ownWorkout")) {
            boolean ownExercise = getIntent().getBooleanExtra("ownWorkout", false);
            if (ownExercise) {
                deleteWorkoutFab.show();
            }
        } else {
            deleteWorkoutFab.hide();
        }


    }

    private void downloadExercises(final Workout workout) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("workout").child(workout.getId()).child("workoutExerciseList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                workoutExerciseList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Exercise exercise = new Exercise();
                    exercise.setName(ds.child("name").getValue().toString());
                    exercise.setSets(ds.child("sets").getValue().toString());
                    exercise.setBodyPart(ds.child("bodyPart").getValue().toString());
                    if (ds.hasChild("reps"))
                        exercise.setReps(ds.child("reps").getValue().toString());
                    if (ds.hasChild("duration"))
                        exercise.setDuration(ds.child("duration").getValue().toString());
                    Log.i(TAG, "onDataChange: " + exercise.toString());
                    workoutExerciseList.add(exercise);
                }
                setupRecycler();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void setupRecycler() {
        SpecificWorkoutAdapter specificWorkoutAdapter = new SpecificWorkoutAdapter(getApplicationContext(),
                new ExerciseInsideWorkoutCallback() {
                    @Override
                    public void onTimeExerciseClicked(Exercise exercise, Integer adapterPosition) {
                        openSelectedExerciseFragment(exercise);
                    }

                    @Override
                    public void onRepsExerciseClicked(Exercise exercise, Integer adapterPosition) {
                        openSelectedExerciseFragment(exercise);
                    }
                });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        specificWorkoutRecycler.setLayoutManager(linearLayoutManager);
        specificWorkoutAdapter.setDataSource(workoutExerciseList);
        specificWorkoutRecycler.setAdapter(specificWorkoutAdapter);
        promptUserToClickExercise();
    }

    private void openSelectedExerciseFragment(Exercise exercise) {
        ExerciseFragment exerciseFragment = ExerciseFragment.newInstance(exercise);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, exerciseFragment).addToBackStack("exerciseFragment").commit();
    }


    @OnClick(R.id.deleteWorkoutFab)
    public void onViewClicked() {
        new AlertDialog.Builder(this)
                .setTitle("Delete workout ?")
                .setMessage("Are you sure you want to delete this workout permanently from app?")
                .setIcon(R.drawable.ic_delete_black_24dp)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteWorkout();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteWorkout() {
        //remove exercise data from db
        final String workoutId = workout.getId();

        final String photoLink = workout.getPhotoLink();

        FirebaseDatabase.getInstance().getReference("workout").child(workoutId).setValue(null); //delete workout data
        FirebaseStorage.getInstance().getReference().child(photoLink).delete();//delete workout photo
        Toast.makeText(SpecificWorkoutActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }


    private void promptUserToClickExercise() {
        /**show view to prompt user to click on exercise if he is first time here*/

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        isnotFirstTimeUser = prefs.getBoolean("isnotFirstTimeUser", false);

        if (isnotFirstTimeUser) return;


        //all below code won't execute if user seen this helper view before
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("isnotFirstTimeUser", true);
        editor.apply();


        TapTargetView.showFor(this,                 // `this` is an Activity
                TapTarget.forView(findViewById(R.id.tapTarget), "Click to view each exercise in detail", "")
                        // All options below are optional
                        .outerCircleColor(R.color.blue)      // Specify a color for the outer circle
                        .titleTextSize(25)                  // Specify the size (in sp) of the title text
                        .descriptionTextSize(18)            // Specify the size (in sp) of the description text
                        .textColor(R.color.grey)            // Specify a color for both the title and description text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                        .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
                        .targetRadius(60));                // Specify the target radius (in dp)

    }


    @Override
    public void openYoutubeFragment(String exerciseName) {
        YoutubeFragment youtubeFragment = YoutubeFragment.newInstance(exerciseName);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, youtubeFragment).addToBackStack("youtubeFragment").commit();
    }

}
