package ganainy.dev.gymmasters.ui.main;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ganainy.dev.gymmasters.ui.main.exercises.MainFragmentExcercies;
import ganainy.dev.gymmasters.ui.main.home.MainFragmentHome;
import ganainy.dev.gymmasters.ui.main.workouts.MainFragmentWorkouts;

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
