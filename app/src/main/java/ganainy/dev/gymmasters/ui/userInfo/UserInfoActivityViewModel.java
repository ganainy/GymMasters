package ganainy.dev.gymmasters.ui.userInfo;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.Workout;

import java.util.List;

public class UserInfoActivityViewModel extends AndroidViewModel {
    private static final String TAG = "UserInfoActivityViewMod";
    private MutableLiveData<List<Exercise>> exercisesList;
    private UserInfoActivityRepository mRepo;
    private MutableLiveData<Uri> profilePhoto;
    private MutableLiveData<List<Workout>> workoutList;
    private MutableLiveData<Boolean> subscribeState;
    private MutableLiveData<String> followersCount;
    private MutableLiveData<Integer> ratingsAvg;
    private MutableLiveData<Long> myRate;
    private MutableLiveData<String> followingCount;
    private MutableLiveData<Boolean> isRateUpdated;

    public UserInfoActivityViewModel(@NonNull Application application) {
        super(application);
        mRepo = UserInfoActivityRepository.getInstance();
    }


    public LiveData<List<Exercise>> getExercises(String profileId) {
        if (exercisesList != null) {
        } else {

            exercisesList = mRepo.getExercises(profileId);
        }
        return exercisesList;
    }

    public LiveData<List<Workout>> getWorkouts(String profileId) {
        if (workoutList != null) {
        } else {
            workoutList = mRepo.getWorkouts(profileId);
        }
        return workoutList;
    }


    public LiveData<Uri> getUserPhoto(String photo) {

        if (profilePhoto != null) {

        } else {
            profilePhoto = mRepo.downloadUserPhoto(photo);
        }
        return profilePhoto;
    }


    public LiveData<Boolean> getFollowState(String profileId) {
        if (subscribeState != null) {

        } else {
            subscribeState = mRepo.getFollowState(profileId);
        }
        return subscribeState;
    }


    public LiveData<String> getFollowersCount(String profileId) {
        if (followersCount != null) {

        } else {
            followersCount = mRepo.getFollowersCount(profileId);
        }
        return followersCount;
    }


    public LiveData<Integer> getRatingsAvg(String profileId) {
        if (ratingsAvg != null) {

        } else {
            ratingsAvg = mRepo.getRatingsAvg(profileId);
        }
        return ratingsAvg;
    }


    public LiveData<Long> getMyRate(String profileId) {
        if (myRate != null) {

        } else {
            myRate = mRepo.getMyRate(profileId);
        }
        return myRate;
    }


    public LiveData<Boolean> setRate(Integer rating, String profileId) {


        isRateUpdated = mRepo.setRate(rating, profileId);
        return isRateUpdated;
    }


    public LiveData<String> getFollowingCount(String profileId) {

        if (followingCount != null) {

        } else {
            followingCount = mRepo.getFollowingCount(profileId);
        }
        return followingCount;
    }
}
