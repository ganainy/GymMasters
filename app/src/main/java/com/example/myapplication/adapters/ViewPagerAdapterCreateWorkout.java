package com.example.myapplication.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.myapplication.fragments.CreateWorkoutFragment1;
import com.example.myapplication.fragments.CreateWorkoutFragment2;

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
