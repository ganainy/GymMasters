package ganainy.dev.gymmasters.ui.userInfo;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.Workout;
import ganainy.dev.gymmasters.utils.AuthUtils;
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

import static ganainy.dev.gymmasters.ui.exercise.ExercisesViewModel.EXERCISES;

public class UserInfoActivityRepository {
    private static final String TAG = "UserInfoActivityReposit";
    private static UserInfoActivityRepository instance;
    private List<Exercise> exerciseList = new ArrayList<>();
    private List<Workout> workoutList = new ArrayList<>();
    private Boolean isSubscribed;
    private String test;
    private int sumRatings;
    private int sumRaters;

    public static UserInfoActivityRepository getInstance() {
        if (instance == null) {
            instance = new UserInfoActivityRepository();
        }
        return instance;
    }

    public MutableLiveData<List<Exercise>> getExercises(final String profileId) {
        final MutableLiveData<List<Exercise>> load = new MutableLiveData<>();
        exerciseList.clear();

        DatabaseReference exerciseNode = FirebaseDatabase.getInstance().getReference(EXERCISES);
        exerciseNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsBig : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds : dsBig.getChildren()) {
                        if (ds.child("creatorId").getValue().equals(profileId)) {
                            final Exercise exercise = new Exercise();
                            exercise.setExecution(ds.child("execution").getValue().toString());
                            exercise.setName(ds.child("name").getValue().toString());
                            if (ds.hasChild("additional_notes"))
                                exercise.setAdditional_notes(ds.child("additional_notes").getValue().toString());
                            exercise.setMechanism(ds.child("mechanism").getValue().toString());
                            exercise.setPreviewPhoto1(ds.child("previewPhoto1").getValue().toString());
                            exercise.setPreviewPhoto2(ds.child("previewPhoto2").getValue().toString());
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


    public MutableLiveData<Uri> downloadUserPhoto(final String photo) {
        final MutableLiveData<Uri> load = new MutableLiveData<>();

        if (photo == null) {
            return load;
        }
                load.setValue(Uri.parse(photo));//photo itself is url(google image)
        return load;
    }

    public MutableLiveData<List<Workout>> getWorkouts(final String profileId) {
        final MutableLiveData<List<Workout>> load = new MutableLiveData<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("workout").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                workoutList.clear();
                if (!dataSnapshot.exists()) {
                    load.setValue(workoutList);
                } else {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        //only show in main list the workouts that admin added
                        if (ds.child("creatorId").getValue().equals(profileId)) {
                            Workout workout = new Workout();
                            workout.setName(ds.child("name").getValue().toString());
                            workout.setDuration(ds.child("duration").getValue().toString() + " mins");
                            workout.setExercisesNumber(ds.child("exercisesNumber").getValue().toString());
                            workout.setPhotoLink(ds.child("photoLink").getValue().toString());
                            workout.setId(ds.child("id").getValue().toString());
                            workout.setLevel(ds.child("level").getValue().toString());

                            workoutList.add(workout);
                        }
                        load.setValue(workoutList);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return load;
    }


    public MutableLiveData<Boolean> getFollowState(String profileId) {


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
                                //todo
                                if (dataSnapshot1.getValue().equals("AuthUtils.loggedInUserId")) {
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

    public MutableLiveData<String> getFollowersCount(String profileId) {
        final MutableLiveData<String> load = new MutableLiveData<>();
        FirebaseDatabase.getInstance().getReference("users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("followersUID")) {
                    load.setValue(String.valueOf(dataSnapshot.child("followersUID").getChildrenCount()));
                } else {
                    load.setValue("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return load;
    }


    public MutableLiveData<Long> getMyRate(String profileId) {
        final MutableLiveData<Long> load = new MutableLiveData<>();


        DatabaseReference ratings = FirebaseDatabase.getInstance().getReference("users").child(profileId).child("Ratings");

        ratings.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //todo
                if (dataSnapshot.hasChild("AuthUtils.loggedInUserId")) {
                    load.setValue((long) dataSnapshot.child("AuthUtils.loggedInUserId").getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return load;

    }


    public MutableLiveData<Integer> getRatingsAvg(String profileId) {

        final MutableLiveData<Integer> load = new MutableLiveData<>();
        final DatabaseReference users = FirebaseDatabase.getInstance().getReference("users").child(profileId);
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("Ratings")) {
                    users.child("Ratings").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            sumRatings = 0;
                            sumRaters = 0;
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                sumRatings += (long) ds.getValue();
                                sumRaters++;
                            }
                            load.setValue(sumRatings / sumRaters);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    load.setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return load;
    }


    public MutableLiveData<Boolean> setRate(Integer rating, String profileId) {
        final MutableLiveData<Boolean> load = new MutableLiveData<>();
        //todo
        FirebaseDatabase.getInstance().getReference("users").child(profileId).child("Ratings").child("AuthUtils.loggedInUserId")
                .setValue(rating).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                load.setValue(true);
            }
        });
        return load;
    }

    public MutableLiveData<String> getFollowingCount(String profileId) {

        final MutableLiveData<String> load = new MutableLiveData<>();
        FirebaseDatabase.getInstance().getReference("users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("followingUID")) {
                    load.setValue(String.valueOf(dataSnapshot.child("followingUID").getChildrenCount()));
                } else {
                    load.setValue("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return load;
    }
}


