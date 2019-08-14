package com.example.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.example.myapplication.activities.AddNewExerciseActivity;
import com.example.myapplication.activities.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainFragmentHome extends Fragment {

    @BindView(R.id.testImage)
    ImageView testImage;

    @OnClick(R.id.createWorkout)
    void createWorkout() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setupViewPagerForCreateWorkout();

     /*  if ( testImage.getVisibility()==View.VISIBLE)
        {
            testImage.setVisibility(View.GONE);
        }else
       {
           testImage.setVisibility(View.VISIBLE);
       }*/

    }


    @OnClick(R.id.createExercise)
    void createExercise() {
        startActivity(new Intent(getActivity(), AddNewExerciseActivity.class));
    }


    public MainFragmentHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
