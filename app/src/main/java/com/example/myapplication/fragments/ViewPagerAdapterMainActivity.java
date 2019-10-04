package com.example.myapplication.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapterMainActivity extends FragmentPagerAdapter {
    private static final String TAG = "ViewPagerAdapterMainAct";
    public ViewPagerAdapterMainActivity(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MainFragmentHome();
            case 1:
                return new MainFragmentExcercies();
            case 2:
                return new MainFragmentWorkouts();
        }
        return null; //does not happen
    }

    @Override
    public int getCount() {
        return 3;
    }
}
