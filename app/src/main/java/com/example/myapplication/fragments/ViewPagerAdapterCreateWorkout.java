package com.example.myapplication.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapterCreateWorkout extends FragmentPagerAdapter {

    public ViewPagerAdapterCreateWorkout(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CreateWorkoutFragment1();
            case 1:
                return new CreateWorkoutFragment2();

        }
        return null; //does not happen
    }

    @Override
    public int getCount() {
        return 2;
    }
}
