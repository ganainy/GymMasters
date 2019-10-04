package com.example.myapplication.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.myapplication.MainActivityRepository;

public class MainActivityViewModel extends AndroidViewModel {


    private final MainActivityRepository mainActivityRepository;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);

         mainActivityRepository=new MainActivityRepository(application);


    }

    public void getUserData(final MainActivity.FirebaseCallback firebaseCallback)
    {
        mainActivityRepository.getUserData(firebaseCallback);
    }
}
