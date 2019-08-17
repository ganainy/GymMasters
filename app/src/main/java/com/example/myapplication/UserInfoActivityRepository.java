package com.example.myapplication;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.myapplication.model.Exercise;
import com.example.myapplication.model.Workout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class UserInfoActivityRepository {
    private static final String TAG = "UserInfoActivityReposit";
    private static UserInfoActivityRepository instance;
    private List<Exercise> exerciseList = new ArrayList<>();
    private List<Workout> workoutList = new ArrayList<>();
    private Boolean isSubscribed;
    private String test;

    public static UserInfoActivityRepository getInstance() {
        if (instance == null) {
            instance = new UserInfoActivityRepository();
        }
        return instance;
    }

    public MutableLiveData<List<Exercise>> getExercises(final String profileId) {
        final MutableLiveData<List<Exercise>> load = new MutableLiveData<>();
        exerciseList.clear();
        final Exercise exercise = new Exercise();
        DatabaseReference exerciseNode = FirebaseDatabase.getInstance().getReference("excercises");
        exerciseNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsBig : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds : dsBig.getChildren()) {
                        if (ds.child("creatorId").getValue().equals(profileId)) {
                            exercise.setExecution(ds.child("execution").getValue().toString());
                            exercise.setPreparation(ds.child("preparation").getValue().toString());
                            exercise.setMechanism(ds.child("mechanism").getValue().toString());
                            exercise.setPreviewPhoto1(ds.child("previewPhoto1").getValue().toString());
                            exercise.setPreviewPhoto2(ds.child("previewPhoto2").getValue().toString());
                            exercise.setUtility(ds.child("utility").getValue().toString());
                            exercise.setVideoLink(ds.child("videoLink").getValue().toString());
                            exerciseList.add(exercise);
                        }
                    }
                }
                load.setValue(exerciseList);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "onCancelled: " + databaseError.getMessage() + " %%% " + databaseError.getDetails());
            }
        });
        load.setValue(exerciseList);
        return load;
    }


    public MutableLiveData<RequestBuilder<Drawable>> downloadUserPhoto(final Application application, String photo) {
        final MutableLiveData<RequestBuilder<Drawable>> load = new MutableLiveData<>();
        FirebaseStorage.getInstance().getReference().child("images/").child(photo).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                RequestBuilder<Drawable> load1 = Glide.with(application.getApplicationContext()).load(uri.toString());
                load.setValue(load1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: " + e.getMessage());
            }
        });

        return load;
    }

    public MutableLiveData<List<Workout>> getWorkouts(final String profileId) {
        final MutableLiveData<List<Workout>> load = new MutableLiveData<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("workout").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                workoutList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //only show in main list the workouts that admin added
                    if (ds.child("creatorId").getValue().equals(profileId)) {
                        Workout workout = new Workout();
                        workout.setName(ds.child("name").getValue().toString());
                        workout.setDuration(ds.child("duration").getValue().toString() + " mins");
                        workout.setExercisesNumber(ds.child("exercisesNumber").getValue().toString());
                        workout.setPhotoLink(ds.child("photoLink").getValue().toString());
                        workout.setId(ds.child("id").getValue().toString());

                        workoutList.add(workout);
                    }
                    load.setValue(workoutList);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return load;
    }


    public MutableLiveData<Boolean> getFollowState(String profileId) {
        Log.i(TAG, "getFollowState: ");
        final MutableLiveData<Boolean> load = new MutableLiveData<>();

        //add logged in user id in the account of the clicked user
        final DatabaseReference profile = FirebaseDatabase.getInstance().getReference("users").child(profileId);
        profile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("followersUID")) {
                    //selected profile has list of followers we will check in it for the logged in user id
                    profile.child("followersUID").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                if (dataSnapshot1.getValue().equals(MyConstant.loggedInUserId)) {
                                    //this means logged in user already subscribed
                                    isSubscribed = true;
                                    break;

                                } else {
                                    isSubscribed = false;
                                }


                            }
                            load.setValue(isSubscribed);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {
                    //selected profile has 0 followers
                    isSubscribed = false;
                    load.setValue(isSubscribed);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return load;
    }

    public MutableLiveData<String> followUnfollow(Boolean isSubscribed, String profileId) {
        Log.i(TAG, "followUnfollow: ");
        final MutableLiveData<String> load = new MutableLiveData<>();

        if (isSubscribed) {
            //get key of the node where logged in user id is saved and delete it
            final DatabaseReference profile = FirebaseDatabase.getInstance().getReference("users")
                    .child(profileId).child("followersUID");
            profile.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.getValue().equals(MyConstant.loggedInUserId)) {
                            String key = dataSnapshot1.getKey();
                            profile.child(key).removeValue();
                            test = "unfollowdone";
                            break;
                        }

                    }
                    load.setValue(test);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        } else {
            //subscribe
            final DatabaseReference profile = FirebaseDatabase.getInstance().getReference("users")
                    .child(profileId).child("followersUID");
            profile.push().setValue(MyConstant.loggedInUserId);
            test = "followdone";
            load.setValue(test);
        }
        return load;
    }
}

