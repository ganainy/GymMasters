package ganainy.dev.gymmasters.ui.userInfo;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.models.app_models.Workout;
import ganainy.dev.gymmasters.utils.AuthUtils;
import ganainy.dev.gymmasters.utils.FirebaseUtils;

import static ganainy.dev.gymmasters.ui.exercise.ExercisesViewModel.EXERCISES;

public class UserInfoViewModel extends ViewModel {
    private static final String TAG = "UserInfoActivityViewMod";
    public static final String CREATOR_ID = "creatorId";
    public static final String WORKOUT = "workout";
    public static final String USERS = "users";
    public static final String FOLLOWERS_UID = "followersUID";
    public static final String RATINGS = "Ratings";
    public static final String FOLLOWING_UID = "followingUID";

    private List<Exercise> exercisesList = new ArrayList<>();
    private List<Workout> workoutList = new ArrayList<>();
    private FollowState followState;
    private Long followersCount;
    private Long ratingAverage;
    private Long followingCount;
    private Long loggedUserRating;
    private User profileOwner;

    private Application app;

    private UserProfileModel userProfileModel = new UserProfileModel();

    public LiveData<UserProfileModel> getUserProfileModelLiveData() {
        return userProfileModelLiveData;
    }

    private MutableLiveData<UserProfileModel> userProfileModelLiveData = new MutableLiveData<>();


    public UserInfoViewModel(@NonNull Application application, User profileOwner) {
        this.app = application;
        this.profileOwner=profileOwner;
        userProfileModel.setProfileOwner(profileOwner);
        followState=FollowState.NOT_FOLLOWING;
        userProfileModel.setFollowState(followState);
        userProfileModelLiveData.setValue(userProfileModel);

        //get workouts list
        getWorkouts();
        //get follow state first time we open activity
        getFollowState();
        //get followers count
        getFollowersCount();
        //get rating average
        getRatingsAvg();
        //get following count
        getFollowingCount();
        //get my rating and show it on rating bar
        getLoggedUserRatingForProfile();
        //download user exercises
        getExercises();
    }


    public void getExercises() {
        if (exercisesList != null) {
            return;
        }

        DatabaseReference exerciseNode = FirebaseDatabase.getInstance().getReference(EXERCISES);
        exerciseNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsBig : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds : dsBig.getChildren()) {
                        if (ds.child(CREATOR_ID).getValue().equals(profileOwner.getId())) {
                            Exercise exercise = FirebaseUtils.getExerciseFromSnapshot(ds);
                            exercisesList.add(exercise);
                        }
                    }
                }
                userProfileModel.setExercisesList(exercisesList);
                userProfileModelLiveData.setValue(userProfileModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "onCancelled: " + databaseError.getMessage() + " %%% " + databaseError.getDetails());
            }
        });

    }

    public void getWorkouts() {
        if (workoutList != null) {
            return;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(WORKOUT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        //only show in main list the workouts that admin added
                        if (ds.child(CREATOR_ID).getValue().equals(profileOwner.getId())) {
                            Workout workout = FirebaseUtils.getWorkoutFromSnapshot(ds);
                            workoutList.add(workout);
                        }
                        userProfileModel.setWorkoutList(workoutList);
                        userProfileModelLiveData.setValue(userProfileModel);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void getFollowState() {

        //add logged in user id in the account of the clicked user
        final DatabaseReference profile = FirebaseDatabase.getInstance().getReference(USERS).child(profileOwner.getId());
        profile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(FOLLOWERS_UID)) {
                    //selected profile has list of followers we will check in it for the logged in user id
                    profile.child(FOLLOWERS_UID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                if (dataSnapshot1.getValue().equals(AuthUtils.getLoggedUserId(app))) {
                                    //this means logged in user already subscribed
                                    followState = FollowState.FOLLOWING;
                                    break;
                                } else {
                                    followState = FollowState.NOT_FOLLOWING;
                                }
                            }
                            userProfileModel.setFollowState(followState);
                            userProfileModelLiveData.setValue(userProfileModel);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            followState = FollowState.ERROR;
                            userProfileModel.setFollowState(followState);
                            userProfileModelLiveData.setValue(userProfileModel);
                        }
                    });

                } else {
                    //selected profile has 0 followers
                    followState = FollowState.NOT_FOLLOWING;
                    userProfileModel.setFollowState(followState);
                    userProfileModelLiveData.setValue(userProfileModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                followState = FollowState.ERROR;
                userProfileModel.setFollowState(followState);
                userProfileModelLiveData.setValue(userProfileModel);
            }
        });

    }

    public void getFollowersCount() {
        if (followersCount != null) {
            return;
        }

        FirebaseDatabase.getInstance().getReference(USERS).child(profileOwner.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(FOLLOWERS_UID)) {
                    followersCount = dataSnapshot.child(FOLLOWERS_UID).getChildrenCount();
                } else {
                    followersCount = 0L;
                }
                userProfileModel.setFollowersCount(followersCount);
                userProfileModelLiveData.setValue(userProfileModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getRatingsAvg() {
        if (ratingAverage != null) {
            return;
        }

        final DatabaseReference users = FirebaseDatabase.getInstance().getReference(USERS).child(profileOwner.getId());
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(RATINGS)) {
                    users.child(RATINGS).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Long sumRatings = 0L;
                            Long sumRaters = 0L;

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                sumRatings += (Long) ds.getValue();
                                sumRaters++;
                            }
                            ratingAverage = sumRatings / sumRaters;
                            userProfileModel.setRatingAverage(ratingAverage);
                            userProfileModelLiveData.setValue(userProfileModel);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    userProfileModel.setRatingAverage(0L);
                    userProfileModelLiveData.setValue(userProfileModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void getFollowingCount() {

        if (followingCount != null) {
            return;
        }
        FirebaseDatabase.getInstance().getReference(USERS).child(profileOwner.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(FOLLOWING_UID)) {
                    followingCount = dataSnapshot.child(FOLLOWING_UID).getChildrenCount();
                } else {
                    followingCount = 0L;
                }
                userProfileModel.setFollowingCount(followingCount);
                userProfileModelLiveData.setValue(userProfileModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getLoggedUserRatingForProfile() {
        if (loggedUserRating != null) {
            return;
        } else {

            DatabaseReference ratings = FirebaseDatabase.getInstance().getReference(USERS).child(profileOwner.getId()).child(RATINGS);

            ratings.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(AuthUtils.getLoggedUserId(app))) {
                        loggedUserRating = (Long) dataSnapshot.child(AuthUtils.getLoggedUserId(app)).getValue();
                        userProfileModel.setLoggedUserRating(loggedUserRating);
                        userProfileModelLiveData.setValue(userProfileModel);
                    } else {
                        //logged user didn't rate yet
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    public void setRate(Long rating) {
        loggedUserRating = rating;
        userProfileModel.setLoggedUserRating(loggedUserRating);
        userProfileModelLiveData.setValue(userProfileModel);
        FirebaseDatabase.getInstance().getReference(USERS).child(profileOwner.getId()).child(RATINGS).child(AuthUtils.getLoggedUserId(app))
                .setValue(rating).addOnSuccessListener(aVoid -> {
            /*logged user rating got saved on server so we call get average rating again to get new rating*/
            getRatingsAvg();
        });
    }

    public void followUnfollow() {
        switch (followState) {

            case FOLLOWING:{
                followState=FollowState.NOT_FOLLOWING;
                unFollow();
                break;
            }
            case NOT_FOLLOWING:{
                followState=FollowState.FOLLOWING;
                follow();
                break;
            }
        }
        userProfileModel.setFollowState(followState);
        userProfileModelLiveData.setValue(userProfileModel);
    }

    private void follow() {
        //add id of logged in user in followersUID in profile account
        final DatabaseReference profile2 = FirebaseDatabase.getInstance().getReference(USERS)
                .child(profileOwner.getId()).child(FOLLOWERS_UID);
        profile2.push().setValue(AuthUtils.getLoggedUserId(app));
        //add id of profile account in followingUID of logged in account
        FirebaseDatabase.getInstance().getReference(USERS).child(AuthUtils.getLoggedUserId(app)).child(FOLLOWING_UID)
                .push().setValue(profileOwner.getId()).addOnSuccessListener(aVoid -> {
            /*follow was successful get new followers count*/
            getFollowersCount();
            followState = FollowState.FOLLOWING;
            userProfileModel.setFollowState(followState);
            userProfileModelLiveData.setValue(userProfileModel);
        }).addOnFailureListener(e -> {
            followState = FollowState.ERROR;
            userProfileModel.setFollowState(followState);
            userProfileModelLiveData.setValue(userProfileModel);
        });
    }

    private void unFollow() {
        final DatabaseReference profile = FirebaseDatabase.getInstance().getReference(USERS)
                .child(profileOwner.getId()).child(FOLLOWERS_UID);
        profile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                    if (ds.getValue().equals(AuthUtils.getLoggedUserId(app))) {
                        String key = ds.getKey();
                        profile.child(key).removeValue();
                        /*unfollow was successful get new followers count*/
                        getFollowersCount();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                followState = FollowState.ERROR;
                userProfileModel.setFollowState(followState);
                userProfileModelLiveData.setValue(userProfileModel);
            }
        });
        //
        final DatabaseReference followingUID = FirebaseDatabase.getInstance().
                getReference(USERS).child(AuthUtils.getLoggedUserId(app)).child(FOLLOWING_UID);
        followingUID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getValue().equals(profileOwner.getId())) {
                        String key = ds.getKey();
                        followingUID.child(key).removeValue();
                        followState = FollowState.NOT_FOLLOWING;
                        userProfileModel.setFollowState(followState);
                        userProfileModelLiveData.setValue(userProfileModel);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                followState = FollowState.ERROR;
                userProfileModel.setFollowState(followState);
                userProfileModelLiveData.setValue(userProfileModel);
            }
        });
    }

}