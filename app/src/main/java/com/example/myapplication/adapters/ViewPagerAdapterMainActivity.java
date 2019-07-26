package com.example.myapplication.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.example.myapplication.fragments.LoginFragment1;
import com.example.myapplication.fragments.LoginFragment2;
import com.example.myapplication.fragments.LoginFragment3;
import com.example.myapplication.fragments.MainFragmentChat;
import com.example.myapplication.fragments.MainFragmentExcercies;
import com.example.myapplication.fragments.MainFragmentWorkouts;

public class ViewPagerAdapterMainActivity extends FragmentPagerAdapter {
    private static final String TAG = "ViewPagerAdapterMainAct";
    public ViewPagerAdapterMainActivity(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Log.i(TAG, "getItem: 1");
                return new MainFragmentWorkouts();

            case 1:
                Log.i(TAG, "getItem: 2");
                return new MainFragmentExcercies();
            case 2:
                Log.i(TAG, "getItem: 3");
                return new MainFragmentChat();
        }
        return null; //does not happen
    }

    @Override
    public int getCount() {
        return 3;
    }
}
