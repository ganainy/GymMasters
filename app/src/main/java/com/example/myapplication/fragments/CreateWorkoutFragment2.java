package com.example.myapplication.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.adapters.ExerciseAdapterAdvanced;
import com.example.myapplication.model.Exercise;
import com.example.myapplication.model.Workout;
import com.example.myapplication.ui.MainActivity;
import com.example.myapplication.utils.MyConstant;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateWorkoutFragment2 extends Fragment {

    private static final String TAG = "CreateWorkoutFragment2";
    @BindView(R.id.hideThisView)
    ConstraintLayout hideThisView;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    private ExerciseAdapterAdvanced exerciseAdapter;
    private Workout workout;
    private List<Exercise> exercisesOfWorkoutList;


    @OnClick(R.id.finishButton)
    void saveWorkout() {

        exercisesOfWorkoutList = exerciseAdapter.getExercisesOfWorkoutList();
        Log.i(TAG, "saveWorkout: " + exercisesOfWorkoutList.size());

        //now we have workout(name,duration,level,image) which came from createworkoutfragment1 delivered by main activity
        //also we have exercisesOfWorkoutList which came from exerciseadapterAdvanced
        //so we can upload workout
        uploadWorkout();
        //go back to main activity
        startActivity(new Intent(getActivity(), MainActivity.class));
        FancyToast.makeText(getActivity(), "Workout created.", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();


    }

    private void uploadWorkout() {
        //save user id with the workout
        workout.setCreatorId(MyConstant.loggedInUserId);
        DatabaseReference workoutRef = FirebaseDatabase.getInstance().getReference("workout");
        String id = workoutRef.push().getKey();
        workout.setId(id);
        workout.setExercisesNumber(String.valueOf(exercisesOfWorkoutList.size()));
        workout.setWorkoutExerciseList(exercisesOfWorkoutList);
        workout.setDate(String.valueOf(System.currentTimeMillis()));
        workoutRef.child(id).setValue(this.workout);
    }

    private List<Exercise> exerciseList = new ArrayList<>();

    public CreateWorkoutFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_workout_fragment2, container, false);
        ButterKnife.bind(this, view);


        ((MainActivity) getActivity()).setOnBundleSelected2(new MainActivity.SelectedBundle2() {
            @Override
            public void onBundleSelect(Bundle bundle) {

                workout = bundle.getParcelable("workout");

                hideThisView.setVisibility(View.GONE);
                downloadAllExercises();
            }
        });

        return view;
    }

    private void downloadAllExercises() {
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Exercise exercise = new Exercise();
                    exercise.setName(ds.child("name").getValue().toString());
                    exercise.setBodyPart(ds.child("bodyPart").getValue().toString());
                    exercise.setPreviewPhoto1(ds.child("previewPhoto1").getValue().toString());
                    exerciseList.add(exercise);

                }

                //downloadExercisesImages();
                setupRecycler();

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
        };


        FirebaseDatabase.getInstance().getReference("excercises").addChildEventListener(childEventListener);

    }


     /*   private void downloadExercisesImages(List<Exercise> exerciseList) {


        setupRecycler();
        setupSearchView();
        }
      */


    private void setupRecycler() {

        exerciseAdapter = new ExerciseAdapterAdvanced(getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        exerciseAdapter.setDataSource(exerciseList);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setAdapter(exerciseAdapter);
    }

}
