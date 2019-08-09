package com.example.myapplication.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.example.myapplication.fragments.SpecificMuscleFragment;

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


        }
        return null;
    }

    @Override
    public int getCount() {
        return 1;
    }
}
