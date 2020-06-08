package ganainy.dev.gymmasters.fragments;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ganainy.dev.gymmasters.model.Exercise;
import ganainy.dev.gymmasters.model.User;
import ganainy.dev.gymmasters.model.Workout;
import ganainy.dev.gymmasters.utils.MyConstant;
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

public class MainFragmentHomeViewModel extends ViewModel {

    private static final String TAG = "MainFragmentHomeViewMod";
    private List<Exercise> myCustomExercisesList;
    private List<Workout> myCustomWorkoutList;
    private User user;
    private long sumRatings, sumRaters;


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
                            if (ds.hasChild("additional_notes"))
                                exercise.setAdditional_notes(ds.child("additional_notes").getValue().toString());
                            exercise.setMechanism(ds.child("mechanism").getValue().toString());
                            exercise.setPreviewPhoto1(ds.child("previewPhoto1").getValue().toString());
                            exercise.setPreviewPhoto2(ds.child("previewPhoto2").getValue().toString());
                            if (ds.hasChild("bodyPart"))
                                exercise.setBodyPart(ds.child("bodyPart").getValue().toString());
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
                        workout.setLevel(ds.child("level").getValue().toString());

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
                if (dataSnapshot.hasChild("photo")) {
                    String photo = dataSnapshot.child("photo").getValue().toString();
                    user.setPhoto(photo);
                }
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
        FirebaseDatabase.getInstance().getReference("users").child(MyConstant.loggedInUserId).child("about_me").setValue(s).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
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


    public MutableLiveData<Uri> downloadUserPhoto(final String photo) {

        final MutableLiveData<Uri> load = new MutableLiveData<>();


        if (photo == null) {
            return load;
        }
        FirebaseStorage.getInstance().getReference().child("images/").child(photo).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                load.setValue(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: " + e.getMessage());
                /**if the photo is from google account it will be cause method to fail since it's not in storage*/
                load.setValue(Uri.parse(photo));
            }
        });

        return load;
    }


    public MutableLiveData<String> getFollowersCount() {
        final MutableLiveData<String> load = new MutableLiveData<>();
        FirebaseDatabase.getInstance().getReference("users").child(MyConstant.loggedInUserId).addValueEventListener(new ValueEventListener() {
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

    public MutableLiveData<Long> getRatingsAvg() {

        final MutableLiveData<Long> load = new MutableLiveData<>();
        final DatabaseReference users = FirebaseDatabase.getInstance().getReference("users").child(MyConstant.loggedInUserId);
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
                    load.setValue(0l);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return load;
    }

    public MutableLiveData<String> getFollowingCount() {

        final MutableLiveData<String> load = new MutableLiveData<>();
        FirebaseDatabase.getInstance().getReference("users").child(MyConstant.loggedInUserId).addValueEventListener(new ValueEventListener() {
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
