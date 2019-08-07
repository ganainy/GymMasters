package com.example.myapplication.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.activities.ExercisesActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragmentExcercies extends Fragment {


    public MainFragmentExcercies() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_main_fragment_excercies, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @OnClick(R.id.triceps)
    void showTricepsExercises() {
        Intent intent = new Intent(getActivity(), ExercisesActivity.class);
        intent.putExtra("triceps", "triceps");
        startActivity(intent);
    }

    //TODO add for other muscles

}
