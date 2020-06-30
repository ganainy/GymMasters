package ganainy.dev.gymmasters.ui.findUser;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.utils.AuthUtils;

public class FindUserViewModel extends AndroidViewModel {

    public static final String FOLLOWING_UID = "followingUID";
    public static final String USERS = "users";
    public static final String ID = "id";
    public static final String FOLLOWERS_UID = "followersUID";
    public static final String RATINGS = "Ratings";

    private long sumRatings;
    private int sumRaters;

    private MutableLiveData<User> userLiveData =new MutableLiveData<>();
    private MutableLiveData<User> userWithRatingLiveData =new MutableLiveData<>();
    private MutableLiveData<User> userWithRatingAndFollowerCountLiveData =new MutableLiveData<>();
    private MutableLiveData<Boolean> loadingLiveData =new MutableLiveData<>();

    LiveData<User> followingUserTransformation;
    LiveData<User> followerUserTransformation;
    LiveData<User> userWithRatingTransformation;
    LiveData<User> userWithRatingAndFollowerCountTransformation;



    private MutableLiveData<String> followingIdLiveData =new MutableLiveData<>();
    private MutableLiveData<String> followerIdLiveData =new MutableLiveData<>();
    private MutableLiveData<NoUsersType> noUsersLiveData=new MutableLiveData<>();

    public FindUserViewModel(@NonNull Application application) {
        super(application);

            userWithRatingTransformation = Transformations.switchMap(userLiveData, user -> {
                getRating(user);
                return userWithRatingLiveData;
            });

            userWithRatingAndFollowerCountTransformation=Transformations.switchMap(userWithRatingLiveData,userWithRating->{
                getFollowersCount(userWithRating);
                return userWithRatingAndFollowerCountLiveData;
            });

            followingUserTransformation =Transformations.switchMap(followingIdLiveData, followingId->{
                getFollowingUserById(followingId);
                return userLiveData;
            });

            followerUserTransformation =Transformations.switchMap(followerIdLiveData, followerId->{
                getFollowerDataById(followerId);
                return userLiveData;
            });

    }


    public void loadFollowingId() {
       loadingLiveData.setValue(true);
        final DatabaseReference users = FirebaseDatabase.getInstance().getReference().child(USERS).child(AuthUtils.getLoggedUserId(getApplication()));
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(FOLLOWING_UID)) {
                    users.child(FOLLOWING_UID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                followingIdLiveData.setValue(ds.getValue().toString());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            loadingLiveData.setValue(false);
                        }
                    });
                } else {
                    //logged in user has no one following him
                    noUsersLiveData.setValue(NoUsersType.NO_FOLLOWING);
                    loadingLiveData.setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //todo FancyToast.makeText(FindUsersActivity.this, "Check network connection and try again.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

            }
        });
    }

  private void getFollowingUserById(String followingId) {
      Log.d("dfq", "getFollowingUserById: ");
        DatabaseReference users = FirebaseDatabase.getInstance().getReference().child(USERS);
        users.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (followingId.equals(ds.getKey())) {
                            User user = ds.getValue(User.class);
                            userLiveData.setValue(user);
                        }
                    loadingLiveData.setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
          //      FancyToast.makeText(FindUsersActivity.this, "Check network connection and try again.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

            }
        });
    }


    public void loadAllUsers() {
       loadingLiveData.setValue(true);
        FirebaseDatabase.getInstance().getReference().child(USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child(ID).getValue().equals(AuthUtils.getLoggedUserId(getApplication()))) {
                        //don't show this user in list since it's the logged in user
                    } else {
                        User user = ds.getValue(User.class);
                        userLiveData.setValue(user);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
             //   FancyToast.makeText(FindUsersActivity.this, "Check network connection and try again.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                loadingLiveData.setValue(false);
            }
        });
    }

   public void loadFollowersIds() {
       loadingLiveData.setValue(true);
        final DatabaseReference users = FirebaseDatabase.getInstance().getReference().child(USERS).child(AuthUtils.getLoggedUserId(getApplication()));
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(FOLLOWERS_UID)) {
                    users.child(FOLLOWERS_UID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                followerIdLiveData.setValue(ds.getValue().toString());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                } else {
                    //logged in user has no followers
                    noUsersLiveData.setValue(NoUsersType.NO_FOLLOWERS);
                    loadingLiveData.setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
               // FancyToast.makeText(FindUsersActivity.this, "Check network connection and try again.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                loadingLiveData.setValue(false);
            }
        });
    }


 private void getFollowerDataById(String followerId) {

        DatabaseReference users = FirebaseDatabase.getInstance().getReference().child(USERS);
        users.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (followerId.equals(ds.getKey())) {
                            User user = ds.getValue(User.class);
                            userLiveData.setValue(user);
                        }
                    loadingLiveData.setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
           //     FancyToast.makeText(FindUsersActivity.this, "Check network connection and try again.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

            }
        });
    }


    private void getFollowersCount(User user) {

        FirebaseDatabase.getInstance().getReference(USERS).child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(FOLLOWERS_UID)) {
                    user.setFollowers(dataSnapshot.child(FOLLOWERS_UID).getChildrenCount());
                } else {
                    user.setFollowers(0L);
                }
                userWithRatingAndFollowerCountLiveData.setValue(user);
                loadingLiveData.setValue(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void getRating(User user){
        final DatabaseReference users = FirebaseDatabase.getInstance().getReference(USERS).child(user.getId());
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(RATINGS)) {
                    users.child(RATINGS).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            sumRatings = 0;
                            sumRaters = 0;
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                sumRatings += (long) ds.getValue();
                                sumRaters++;
                            }
                            user.setRating(sumRatings / sumRaters);
                            userWithRatingLiveData.setValue(user);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    user.setRating(0L);
                    userWithRatingLiveData.setValue(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public LiveData<NoUsersType> getNoUsersLiveData() {
        return noUsersLiveData;
    }
    public LiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }


}
