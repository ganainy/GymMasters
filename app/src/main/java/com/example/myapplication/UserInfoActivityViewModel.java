package com.example.myapplication;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;

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
    private MutableLiveData<String> notifyString;

    public UserInfoActivityViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }


    public LiveData<List<Exercise>> getExercises(String profileId) {
        if (exercisesList != null) {
        } else {
            mRepo = UserInfoActivityRepository.getInstance();
            exercisesList = mRepo.getExercises(profileId);
        }
        return exercisesList;
    }

    public LiveData<List<Workout>> getWorkouts(String profileId) {
        if (workoutList != null) {
        } else {
            mRepo = UserInfoActivityRepository.getInstance();
            workoutList = mRepo.getWorkouts(profileId);
        }
        return workoutList;
    }


    public LiveData<RequestBuilder<Drawable>> getUserPhoto(String photo) {

        if (profilePhoto != null) {

        } else {
            Log.i(TAG, "getUserPhoto: ");
            mRepo = UserInfoActivityRepository.getInstance();
            profilePhoto = mRepo.downloadUserPhoto(application, photo);
        }
        return profilePhoto;
    }


    public LiveData<Boolean> getFollowState(String profileId) {
        Log.i(TAG, "getFollowState: ");
        if (subscribeState != null) {

        } else {
            mRepo = UserInfoActivityRepository.getInstance();
        subscribeState = mRepo.getFollowState(profileId);
        }
        return subscribeState;
    }

    public LiveData<String> followUnfollow(Boolean isSubscribed, String profileId) {
        Log.i(TAG, "followUnfollow: ");
        if (notifyString != null) {

        } else {
        mRepo = UserInfoActivityRepository.getInstance();
        notifyString = mRepo.followUnfollow(isSubscribed, profileId);
        }
        return notifyString;
    }


}
