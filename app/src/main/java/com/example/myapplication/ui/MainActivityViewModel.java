package com.example.myapplication.ui;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myapplication.MainActivityRepository;
import com.example.myapplication.model.User;

public class MainActivityViewModel extends AndroidViewModel {


    private final MainActivityRepository mainActivityRepository;
    private LiveData<User> userData;
    private LiveData<Uri> userPhoto;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);

         mainActivityRepository=new MainActivityRepository(application);


    }

    public LiveData<User> getUserData()
    {
        if (userData == null) {
            userData = mainActivityRepository.getUserData();
        }
        return userData;
    }

    public LiveData<Uri> getUserPhoto(User user) {
        if (userPhoto == null) {
            userPhoto = mainActivityRepository.getUserPhoto(user);
        }
        return userPhoto;
    }
}
