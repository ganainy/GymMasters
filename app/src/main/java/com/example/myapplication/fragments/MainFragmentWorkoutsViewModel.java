package com.example.myapplication.fragments;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.model.Workout;
import com.example.myapplication.utils.MyConstant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainFragmentWorkoutsViewModel extends ViewModel {
    private static final String TAG = "MainFragmentWorkoutsVie";
    private List<Workout> workoutList = new ArrayList<>();
    private MutableLiveData<List<Workout>> loadWorkoutList = new MutableLiveData<>();


    public LiveData<List<Workout>> downloadWorkout() {
        if (workoutList.size() > 0) {
            Log.i(TAG, "downloadWorkout: no load");
            return loadWorkoutList;
        }
        Log.i(TAG, "downloadWorkout: load");
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
                loadWorkoutList.setValue(workoutList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadWorkoutList.setValue(workoutList);
            }
        });
        return loadWorkoutList;
    }

}
