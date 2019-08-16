package com.example.myapplication;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.RequestBuilder;
import com.example.myapplication.model.Exercise;

import java.util.List;

public class UserInfoActivityViewModel extends AndroidViewModel {
    private static final String TAG = "UserInfoActivityViewMod";
    private final Application application;
    private MutableLiveData<List<Exercise>> exercisesList;
    private UserInfoActivityRepository mRepo;
    private MutableLiveData<RequestBuilder<Drawable>> requestBuilderMutableLiveData;

    public UserInfoActivityViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public void init(String profileId, String photo) {
        if (exercisesList != null) {
            return;
        } else {

            mRepo = UserInfoActivityRepository.getInstance();
            exercisesList = mRepo.getExercises(profileId);
        }

        if (requestBuilderMutableLiveData != null) {
            return;
        } else {
            mRepo = UserInfoActivityRepository.getInstance();
            requestBuilderMutableLiveData = mRepo.downloadUserPhoto(application, photo);
        }

    }


    public LiveData<List<Exercise>> getExercises() {
        return exercisesList;
    }

    public LiveData<RequestBuilder<Drawable>> getUserPhoto() {
        return requestBuilderMutableLiveData;
    }


}
