package com.example.myapplication.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.example.myapplication.fragments.FavouriteSpecificMuscleFragment;
import com.example.myapplication.fragments.MainFragmentExcercies;
import com.example.myapplication.fragments.MainFragmentWorkouts;
import com.example.myapplication.fragments.SpecificMuscleFragment;

import java.util.List;

public class SpecificMusclePagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "SpecificMusclePagerAdap";

    public SpecificMusclePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Log.i(TAG, "getItem: 1");
                return new SpecificMuscleFragment();

            case 1:
                Log.i(TAG, "getItem: 2");
                return new FavouriteSpecificMuscleFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
