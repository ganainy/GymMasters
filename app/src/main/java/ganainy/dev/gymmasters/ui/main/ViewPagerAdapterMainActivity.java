package ganainy.dev.gymmasters.ui.main;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ganainy.dev.gymmasters.ui.main.exercisesCategories.ExercisesCategoriesFragment;
import ganainy.dev.gymmasters.ui.main.home.HomeFragment;
import ganainy.dev.gymmasters.ui.main.workouts.WorkoutsFragment;

import static ganainy.dev.gymmasters.ui.main.home.HomeFragment.LOGGED_USER_ID;

public class ViewPagerAdapterMainActivity extends FragmentPagerAdapter {
    private static final String TAG = "ViewPagerAdapterMainAct";
    public ViewPagerAdapterMainActivity(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new ExercisesCategoriesFragment();
            case 2:
                return  new WorkoutsFragment();
        }
        return null; //does not happen
    }

    @Override
    public int getCount() {
        return 3;
    }
}
