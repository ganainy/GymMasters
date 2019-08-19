package com.example.myapplication;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.RequestBuilder;
import com.example.myapplication.model.Exercise;
import com.example.myapplication.model.Workout;

import java.util.List;

public class UserInfoActivityViewModel extends AndroidViewModel {
    private static final String TAG = "UserInfoActivityViewMod";
    private final Application application;
    private MutableLiveData<List<Exercise>> exercisesList;
    private UserInfoActivityRepository mRepo;
    private MutableLiveData<RequestBuilder<Drawable>> profilePhoto;
    private MutableLiveData<List<Workout>> workoutList;
    private MutableLiveData<Boolean> subscribeState;
    private MutableLiveData<String> followersCount;
    private MutableLiveData<Integer> ratingsAvg;
    private MutableLiveData<Long> myRate;

    public UserInfoActivityViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
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


    public LiveData<RequestBuilder<Drawable>> getUserPhoto(String photo) {

        if (profilePhoto != null) {

        } else {
            profilePhoto = mRepo.downloadUserPhoto(application, photo);
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


    public void setRate(Integer rating, String profileId) {
        mRepo.setRate(rating, profileId);
    }
}
