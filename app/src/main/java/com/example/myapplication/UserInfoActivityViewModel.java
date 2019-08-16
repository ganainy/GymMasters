package com.example.myapplication;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.myapplication.model.Exercise;

import java.util.List;

public class UserInfoActivityViewModel extends ViewModel {

    private MutableLiveData<List<Exercise>> exercisesList;
    private UserInfoActivityRepository mRepo;
    private String profileId;

    public void init(String profileId) {
        if (exercisesList != null) {
            return;
        } else {
            this.profileId = profileId;
            mRepo = UserInfoActivityRepository.getInstance();
            exercisesList = mRepo.getExercises(profileId);
        }
    }


    public LiveData<List<Exercise>> getExercises() {

        return exercisesList;
    }
}
