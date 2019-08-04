package com.example.myapplication.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.example.myapplication.fragments.LoginFragment1;
import com.example.myapplication.fragments.LoginFragment2;
import com.example.myapplication.fragments.LoginFragment3;


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