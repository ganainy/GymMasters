package ganainy.dev.gymmasters.ui.welcome;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


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
                return new WelcomeFragment1();

            case 1:
                Log.i(TAG, "getItem: 2");
                return new WelcomeFragment2();
            case 2:
                Log.i(TAG, "getItem: 3");
                return new WelcomeFragment3();
        }
        return null; //does not happen
    }

    @Override
    public int getCount() {
        return 3; //three fragments
    }
}
