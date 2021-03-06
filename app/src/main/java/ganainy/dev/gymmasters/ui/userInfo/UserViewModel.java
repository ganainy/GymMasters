package ganainy.dev.gymmasters.ui.userInfo;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.models.app_models.UserProfile;
import ganainy.dev.gymmasters.models.app_models.Workout;
import ganainy.dev.gymmasters.utils.AuthUtils;

import static ganainy.dev.gymmasters.utils.Constants.EXERCISES;

public class UserViewModel extends ViewModel {
    private static final String TAG = "UserInfoActivityViewMod";
    public static final String CREATOR_ID = "creatorId";
    public static final String WORKOUT = "workout";
    public static final String USERS = "users";
    public static final String FOLLOWERS_UID = "followersUID";
    public static final String RATINGS = "Ratings";
    public static final String FOLLOWING_UID = "followingUID";

    private List<Exercise> exercisesList ;
    private List<Workout> workoutList ;
    private FollowState followState;
    private Long followersCount;
    private Long ratingAverage;
    private Long followingCount;
    private Long loggedUserRating;
    private User profileOwner;

    private Application app;

    public UserProfile getUserProfile() {
        return userProfile;
    }

    private UserProfile userProfile = new UserProfile();

    public LiveData<UserProfile> getUserProfileModelLiveData() {
        return userProfileModelLiveData;
    }

    private MutableLiveData<UserProfile> userProfileModelLiveData = new MutableLiveData<>();


    public UserViewModel(@NonNull Application application, User profileOwner) {
        this.app = application;
        this.profileOwner=profileOwner;
        userProfile.setProfileOwner(profileOwner);
        followState=FollowState.NOT_FOLLOWING;
        userProfile.setFollowState(followState);
        userProfileModelLiveData.setValue(userProfile);

        //get workouts list
        getWorkouts();
        //get follow_black state first time we open activity
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
        exercisesList=new ArrayList<>();

        DatabaseReference exerciseNode = FirebaseDatabase.getInstance().getReference(EXERCISES);
        exerciseNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot exerciseSnapShot : dataSnapshot.getChildren()) {
                        if (exerciseSnapShot.child(CREATOR_ID).getValue().equals(profileOwner.getId())) {
                            Exercise exercise = exerciseSnapShot.getValue(Exercise.class);
                            exercisesList.add(exercise);
                        }
                }
                userProfile.setExercisesList(exercisesList);
                userProfileModelLiveData.setValue(userProfile);
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
        workoutList=new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(WORKOUT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot workoutSnapshot : dataSnapshot.getChildren()) {
                        //only show in main list the workouts that admin added
                        if (workoutSnapshot.child(CREATOR_ID).getValue().equals(profileOwner.getId())) {
                            Workout workout = workoutSnapshot.getValue(Workout.class);
                            workoutList.add(workout);
                        }
                        userProfile.setWorkoutList(workoutList);
                        userProfileModelLiveData.setValue(userProfile);
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
                            userProfile.setFollowState(followState);
                            userProfileModelLiveData.setValue(userProfile);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            followState = FollowState.ERROR;
                            userProfile.setFollowState(followState);
                            userProfileModelLiveData.setValue(userProfile);
                        }
                    });

                } else {
                    //selected profile has 0 followers
                    followState = FollowState.NOT_FOLLOWING;
                    userProfile.setFollowState(followState);
                    userProfileModelLiveData.setValue(userProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                followState = FollowState.ERROR;
                userProfile.setFollowState(followState);
                userProfileModelLiveData.setValue(userProfile);
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
                userProfile.setFollowersCount(followersCount);
                userProfileModelLiveData.setValue(userProfile);
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
                            userProfile.setRatingAverage(ratingAverage);
                            userProfileModelLiveData.setValue(userProfile);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    userProfile.setRatingAverage(0L);
                    userProfileModelLiveData.setValue(userProfile);
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
                userProfile.setFollowingCount(followingCount);
                userProfileModelLiveData.setValue(userProfile);
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
                        userProfile.setLoggedUserRating(loggedUserRating);
                        userProfileModelLiveData.setValue(userProfile);
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
        userProfile.setLoggedUserRating(loggedUserRating);
        userProfileModelLiveData.setValue(userProfile);
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
        userProfile.setFollowState(followState);
        userProfileModelLiveData.setValue(userProfile);
    }

    private void follow() {
        //add id of logged in user in followersUID in profile account
        final DatabaseReference profile2 = FirebaseDatabase.getInstance().getReference(USERS)
                .child(profileOwner.getId()).child(FOLLOWERS_UID);
        profile2.push().setValue(AuthUtils.getLoggedUserId(app));
        //add id of profile account in followingUID of logged in account
        FirebaseDatabase.getInstance().getReference(USERS).child(AuthUtils.getLoggedUserId(app)).child(FOLLOWING_UID)
                .push().setValue(profileOwner.getId()).addOnSuccessListener(aVoid -> {
            /*follow_black was successful get new followers count*/
            getFollowersCount();
            followState = FollowState.FOLLOWING;
            userProfile.setFollowState(followState);
            userProfileModelLiveData.setValue(userProfile);
        }).addOnFailureListener(e -> {
            followState = FollowState.ERROR;
            userProfile.setFollowState(followState);
            userProfileModelLiveData.setValue(userProfile);
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
                userProfile.setFollowState(followState);
                userProfileModelLiveData.setValue(userProfile);
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
                        userProfile.setFollowState(followState);
                        userProfileModelLiveData.setValue(userProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                followState = FollowState.ERROR;
                userProfile.setFollowState(followState);
                userProfileModelLiveData.setValue(userProfile);
            }
        });
    }

}