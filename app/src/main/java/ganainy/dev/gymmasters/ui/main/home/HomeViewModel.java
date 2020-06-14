package ganainy.dev.gymmasters.ui.main.home;

import android.net.Uri;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.utils.AuthUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import static ganainy.dev.gymmasters.utils.FirebaseUtils.getUserFromSnapshot;

public class HomeViewModel extends ViewModel {

    private static final String TAG = "MainFragmentHomeViewMod";
    public static final String FOLLOWERS_UID = "followersUID";
    public static final String USERS = "users";
    public static final String RATINGS = "Ratings";
    public static final String FOLLOWING_UID = "followingUID";
    public static final String ABOUT_ME = "about_me";
    public static final String IMAGES = "images/";
    private long sumRatings, sumRaters;

    private MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private MutableLiveData<Uri> imageUriLiveData = new MutableLiveData<>();
    private MutableLiveData<String> followersCountLiveData = new MutableLiveData<>();
    private MutableLiveData<Pair<Boolean,String>> updateAboutMe = new MutableLiveData<>();
    private MutableLiveData<String> followingCountLiveData = new MutableLiveData<>();
    private MutableLiveData<Long> ratingAverageLiveData = new MutableLiveData<>();


    public LiveData<Long> getRatingAverageLiveData() {
        return ratingAverageLiveData;
    }



    public LiveData<String> getFollowingCountLiveData() {
        return followingCountLiveData;
    }



    public LiveData<Pair<Boolean, String>> getUpdateAboutMe() {
        return updateAboutMe;
    }


    public LiveData<String> getFollowersCountLiveData() {
        return followersCountLiveData;
    }


    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<Uri> getImageUriLiveData() {
        return imageUriLiveData;
    }


    public void getUserData(final String loggedUserId) {

        FirebaseDatabase.getInstance().getReference(USERS).child(loggedUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = getUserFromSnapshot(dataSnapshot);
                userLiveData.setValue(user);
                downloadUserPhoto(user.getPhoto());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public void updateAboutMe(String newAboutMe, String loggedUserId) {
        FirebaseDatabase.getInstance().getReference(USERS)
                .child(loggedUserId).child(ABOUT_ME)
                .setValue(newAboutMe).addOnSuccessListener(aVoid ->
                updateAboutMe.setValue(new Pair<>(true,newAboutMe)))
                .addOnFailureListener(e -> updateAboutMe.setValue(new Pair<>(false,"")));
    }


    public void downloadUserPhoto(final String photo) {
        if (photo==null)return;

        FirebaseStorage.getInstance().getReference().child(IMAGES).child(photo).getDownloadUrl()
                .addOnSuccessListener(uri ->
                imageUriLiveData.setValue(uri)).addOnFailureListener(e -> {
            /*if the photo is from google account it will be cause method to fail since it's not in storage*/
            imageUriLiveData.setValue(Uri.parse(photo));
        });
    }


    public void getFollowersCount(String loggedUserId) {
        FirebaseDatabase.getInstance().getReference(USERS).child(loggedUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(FOLLOWERS_UID)) {
                    followersCountLiveData.setValue(String.valueOf(dataSnapshot.child(FOLLOWERS_UID).getChildrenCount()));
                } else {
                    followersCountLiveData.setValue("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getRatingsAvg(String loggedUserId) {

        final DatabaseReference users = FirebaseDatabase.getInstance().getReference(USERS).child(loggedUserId);
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(RATINGS)) {
                    users.child(RATINGS).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            sumRatings = 0;
                            sumRaters = 0;
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                sumRatings += (long) ds.getValue();
                                sumRaters++;
                            }
                            ratingAverageLiveData.setValue(sumRatings / sumRaters);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    ratingAverageLiveData.setValue(0L);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getFollowingCount(String loggedUserId) {

        FirebaseDatabase.getInstance().getReference(USERS).child(loggedUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(FOLLOWING_UID)) {
                    followingCountLiveData.setValue(String.valueOf(dataSnapshot.child(FOLLOWING_UID).getChildrenCount()));
                } else {
                    followingCountLiveData.setValue("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
