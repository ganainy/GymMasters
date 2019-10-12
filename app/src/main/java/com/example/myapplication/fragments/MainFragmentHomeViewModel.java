package com.example.myapplication.fragments;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.model.Exercise;
import com.example.myapplication.model.User;
import com.example.myapplication.model.Workout;
import com.example.myapplication.utils.MyConstant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainFragmentHomeViewModel extends ViewModel {

    private static final String TAG = "MainFragmentHomeViewMod";
    private List<Exercise> myCustomExercisesList;
    private List<Workout> myCustomWorkoutList;
    private User user;


    public LiveData<List<Exercise>> downloadMyExercises() {
        final MutableLiveData<List<Exercise>> load = new MutableLiveData<>();


        if (myCustomExercisesList != null) {
            load.setValue(myCustomExercisesList);
            return load;
        }

        myCustomExercisesList = new ArrayList<>();
        final DatabaseReference exerciseNode = FirebaseDatabase.getInstance().getReference("excercises");
        exerciseNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myCustomExercisesList.clear();
                for (DataSnapshot dsBig : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds : dsBig.getChildren()) {
                        Exercise exercise = new Exercise();
                        if (ds.child("creatorId").getValue().equals(MyConstant.loggedInUserId)) {
                            exercise.setName(ds.child("name").getValue().toString());
                            exercise.setExecution(ds.child("execution").getValue().toString());
                            exercise.setPreparation(ds.child("preparation").getValue().toString());
                            exercise.setMechanism(ds.child("mechanism").getValue().toString());
                            exercise.setPreviewPhoto1(ds.child("previewPhoto1").getValue().toString());
                            exercise.setPreviewPhoto2(ds.child("previewPhoto2").getValue().toString());
                            exercise.setUtility(ds.child("utility").getValue().toString());
                            myCustomExercisesList.add(exercise);
                        }
                    }
                }
                load.setValue(myCustomExercisesList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return load;
    }


    public LiveData<List<Workout>> downloadMyWorkout() {
        final MutableLiveData<List<Workout>> load = new MutableLiveData<>();


        if (myCustomWorkoutList != null) {

            load.setValue(myCustomWorkoutList);
            return load;

        }

        myCustomWorkoutList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("workout").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myCustomWorkoutList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("creatorId").getValue().equals(MyConstant.loggedInUserId)) {
                        Workout workout = new Workout();
                        workout.setName(ds.child("name").getValue().toString());
                        workout.setDuration(ds.child("duration").getValue().toString() + " mins");
                        workout.setExercisesNumber(ds.child("exercisesNumber").getValue().toString());
                        workout.setPhotoLink(ds.child("photoLink").getValue().toString());
                        workout.setId(ds.child("id").getValue().toString());

                        myCustomWorkoutList.add(workout);
                    }
                }
                load.setValue(myCustomWorkoutList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return load;
    }


    public LiveData<User> getUserData(final String id) {
        final MutableLiveData<User> load = new MutableLiveData<>();
        if (user != null) {
            load.setValue(user);
            return load;
        }

        user = new User();
        FirebaseDatabase.getInstance().getReference("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                user.setName(name);
                String email = dataSnapshot.child("email").getValue().toString();
                user.setEmail(email);
                if (dataSnapshot.hasChild("about_me")) {
                    String about_me = dataSnapshot.child("about_me").getValue().toString();
                    user.setAbout_me(about_me);
                }

                load.setValue(user);
                Log.i(TAG, "onChildAdded: " + dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return load;
    }

    public LiveData<Boolean> updateAboutMe(String s) {
        final MutableLiveData<Boolean> load = new MutableLiveData<>();
        FirebaseDatabase.getInstance().getReference("users").child(MyConstant.loggedInUserId).child("about_me").setValue(s).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                load.setValue(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                load.setValue(false);
            }
        });
        return load;
    }
}
