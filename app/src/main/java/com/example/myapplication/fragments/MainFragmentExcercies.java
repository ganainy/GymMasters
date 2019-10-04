package com.example.myapplication.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.ui.ExercisesActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragmentExcercies extends Fragment {


    public MainFragmentExcercies() {
        // Required empty public constructorp
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
    void q() {
        Intent intent = new Intent(getActivity(), ExercisesActivity.class);
        intent.putExtra("triceps", "triceps");
        startActivity(intent);
    }

    @OnClick(R.id.chest)
    void qq() {
        Intent intent = new Intent(getActivity(), ExercisesActivity.class);
        intent.putExtra("chest", "chest");
        startActivity(intent);
    }

    @OnClick(R.id.shoulders)
    void qqq() {
        Intent intent = new Intent(getActivity(), ExercisesActivity.class);
        intent.putExtra("shoulders", "shoulders");
        startActivity(intent);
    }

    @OnClick(R.id.biceps)
    void qqqq() {
        Intent intent = new Intent(getActivity(), ExercisesActivity.class);
        intent.putExtra("biceps", "biceps");
        startActivity(intent);
    }

    @OnClick(R.id.abs)
    void qqqqq() {
        Intent intent = new Intent(getActivity(), ExercisesActivity.class);
        intent.putExtra("abs", "abs");
        startActivity(intent);
    }

    @OnClick(R.id.back)
    void qqqqqq() {
        Intent intent = new Intent(getActivity(), ExercisesActivity.class);
        intent.putExtra("back", "back");
        startActivity(intent);
    }



    @OnClick(R.id.cardio)
    void qqqqqqqqqq() {
        Intent intent = new Intent(getActivity(), ExercisesActivity.class);
        intent.putExtra("cardio", "cardio");
        startActivity(intent);
    }

    @OnClick(R.id.leg)
    void qqqqqqqqqqq() {
        Intent intent = new Intent(getActivity(), ExercisesActivity.class);
        intent.putExtra("leg", "leg");
        startActivity(intent);
    }

    @OnClick(R.id.showall)
    void qqqqqqqqqqqq() {
        Intent intent = new Intent(getActivity(), ExercisesActivity.class);
        intent.putExtra("showall", "showall");
        startActivity(intent);
    }




}
