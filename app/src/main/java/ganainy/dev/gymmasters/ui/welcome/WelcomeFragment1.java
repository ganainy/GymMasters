package ganainy.dev.gymmasters.ui.welcome;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import ganainy.dev.gymmasters.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragment1 extends Fragment {


    public WelcomeFragment1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.viewpager_login_1, container, false);
    }

}
