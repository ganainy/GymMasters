package com.example.myapplication.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.myapplication.R;
import com.example.myapplication.adapters.SpecificWorkoutAdapter;
import com.example.myapplication.model.Workout;
import com.example.myapplication.model.WorkoutExercise;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpecificWorkoutActivity extends AppCompatActivity {
    private static final String TAG = "SpecificWorkoutActivity";
    @BindView(R.id.specificWorkoutRecycler)
    RecyclerView specificWorkoutRecycler;
    private WorkoutExercise workoutExercise;
    private List<WorkoutExercise> workoutExerciseList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_workout);
        ButterKnife.bind(this);

        //get workout info and pass it to downloadExercise method
        downloadExercises((Workout) getIntent().getParcelableExtra("workout"));

    }

    private void downloadExercises(final Workout workout) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("workout").child(workout.getId()).child("workoutExerciseList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                workoutExercise = new WorkoutExercise();
                workoutExerciseList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    workoutExercise.setName(ds.child("name").getValue().toString());
                    workoutExercise.setSets(ds.child("sets").getValue().toString());
                    workoutExercise.setTargetMuscle(ds.child("bodyPart").getValue().toString());
                    if (ds.hasChild("reps"))
                        workoutExercise.setReps(ds.child("reps").getValue().toString());
                    if (ds.hasChild("duration"))
                        workoutExercise.setDuration(ds.child("duration").getValue().toString());
                    workoutExerciseList.add(workoutExercise);
                }
                Log.i(TAG, "onDataChange: " + workoutExerciseList.size());
                setupRecycler();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void setupRecycler() {
        SpecificWorkoutAdapter specificWorkoutAdapter = new SpecificWorkoutAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        specificWorkoutRecycler.setLayoutManager(linearLayoutManager);
        specificWorkoutAdapter.setDataSource(workoutExerciseList);
        specificWorkoutRecycler.setAdapter(specificWorkoutAdapter);
    }
}
