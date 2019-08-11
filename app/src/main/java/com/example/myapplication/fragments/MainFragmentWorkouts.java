package com.example.myapplication.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.adapters.WorkoutAdapter;
import com.example.myapplication.model.Exercise;
import com.example.myapplication.model.Workout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragmentWorkouts extends Fragment {

    private static final String TAG = "MainFragmentWorkouts";
    private List<Workout> workoutList = new ArrayList<>();
    private View view;
    private WorkoutAdapter workoutAdapter;

    public MainFragmentWorkouts() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_fragment_workouts, container, false);
        this.view = view;

        downloadWorkout();

        return view;
    }


    private void downloadWorkout() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("workout").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                workoutList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Workout workout = new Workout();
                    workout.setName(ds.child("name").getValue().toString());
                    workout.setDuration(ds.child("duration").getValue().toString() + " mins");
                    workout.setExercisesNumber(ds.child("exercisesNumber").getValue().toString());
                    workout.setPhotoLink(ds.child("photoLink").getValue().toString());
                    //
                    final List<Exercise> workoutExerciseList = new ArrayList<>();
                    ds.child("workoutExerciseList").getRef().addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Exercise exercise = new Exercise();
                            exercise.setBodyPart(dataSnapshot.child("bodyPart").getValue().toString());
                            exercise.setName(dataSnapshot.child("name").getValue().toString());
                            exercise.setPreviewPhoto1(dataSnapshot.child("previewPhoto1").getValue().toString());
                            exercise.setReps(dataSnapshot.child("reps").getValue().toString());
                            exercise.setSets(dataSnapshot.child("sets").getValue().toString());
                            workoutExerciseList.add(exercise);

                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    //

                    Log.i(TAG, "onChildAdded: " + workoutExerciseList.size());
                    workout.setWorkoutExerciseList(workoutExerciseList);
                    workoutList.add(workout);
                }
                setupRecycler();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void setupRecycler() {
        RecyclerView recyclerView = view.findViewById(R.id.workoutRecyclerView);
        workoutAdapter = new WorkoutAdapter(getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        workoutAdapter.setDataSource(workoutList);
        recyclerView.setAdapter(workoutAdapter);
    }


}
