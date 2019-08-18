package com.example.myapplication.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.MyConstant;
import com.example.myapplication.R;
import com.example.myapplication.adapters.WorkoutAdapter;
import com.example.myapplication.model.Workout;
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
                    //only show in main list the workouts that admin added
                    if (ds.child("creatorId").getValue().equals(MyConstant.AdminId)) {
                        Workout workout = new Workout();
                        workout.setName(ds.child("name").getValue().toString());
                        workout.setDuration(ds.child("duration").getValue().toString() + " mins");
                        workout.setExercisesNumber(ds.child("exercisesNumber").getValue().toString());
                        workout.setPhotoLink(ds.child("photoLink").getValue().toString());
                        workout.setId(ds.child("id").getValue().toString());

                        workoutList.add(workout);
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
        RecyclerView recyclerView = view.findViewById(R.id.workoutRecyclerView);
        workoutAdapter = new WorkoutAdapter(getActivity(), "fragmentWorkouts");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        workoutAdapter.setDataSource(workoutList);
        recyclerView.setAdapter(workoutAdapter);
    }


}
