package com.example.myapplication.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;


public class ViewPagerAdapterWelcomeActivity extends FragmentPagerAdapter {
    public ViewPagerAdapterWelcomeActivity(FragmentManager fm) {
        super(fm);
    }

    private static final String TAG = "ViewPagerAdapterWelcome";
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Log.i(TAG, "getItem: 1");
                return new LoginFragment1();

            case 1:
                Log.i(TAG, "getItem: 2");
                return new LoginFragment2();
            case 2:
                Log.i(TAG, "getItem: 3");
                return new LoginFragment3();
        }
        return null; //does not happen
    }

    @Override
    public int getCount() {
        return 3; //three fragments
    }
}
