package com.example.myapplication;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.myapplication.activities.MainActivity;

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
