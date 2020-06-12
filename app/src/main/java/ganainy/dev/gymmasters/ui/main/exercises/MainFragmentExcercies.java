package ganainy.dev.gymmasters.ui.main.exercises;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.ui.exercise.ExercisesActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragmentExcercies extends Fragment {


    public static final String SELECTED_MUSCLE = "selectedMuscle";
    public static final String TRICEPS = "triceps";
    public static final String CHEST = "chest";
    public static final String SHOULDER = "shoulder";
    public static final String BICEPS = "biceps";
    public static final String ABS = "abs";
    public static final String BACK = "back";
    public static final String CARDIO = "cardio";
    public static final String LOWERLEG = "lowerleg";
    public static final String SHOWALL = "showall";

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
        intent.putExtra(SELECTED_MUSCLE, TRICEPS);
        startActivity(intent);
    }

    @OnClick(R.id.chest)
    void qq() {
        Intent intent = new Intent(getActivity(), ExercisesActivity.class);
        intent.putExtra(SELECTED_MUSCLE, CHEST);
        startActivity(intent);
    }

    @OnClick(R.id.shoulders)
    void qqq() {
        Intent intent = new Intent(getActivity(), ExercisesActivity.class);
        intent.putExtra(SELECTED_MUSCLE, SHOULDER);
        startActivity(intent);
    }

    @OnClick(R.id.biceps)
    void qqqq() {
        Intent intent = new Intent(getActivity(), ExercisesActivity.class);
        intent.putExtra(SELECTED_MUSCLE, BICEPS);
        startActivity(intent);
    }

    @OnClick(R.id.abs)
    void qqqqq() {
        Intent intent = new Intent(getActivity(), ExercisesActivity.class);
        intent.putExtra(SELECTED_MUSCLE, ABS);
        startActivity(intent);
    }

    @OnClick(R.id.back)
    void qqqqqq() {
        Intent intent = new Intent(getActivity(), ExercisesActivity.class);
        intent.putExtra(SELECTED_MUSCLE, BACK);
        startActivity(intent);
    }



    @OnClick(R.id.cardio)
    void qqqqqqqqqq() {
        Intent intent = new Intent(getActivity(), ExercisesActivity.class);
        intent.putExtra(SELECTED_MUSCLE, CARDIO);
        startActivity(intent);
    }

    @OnClick(R.id.leg)
    void qqqqqqqqqqq() {
        Intent intent = new Intent(getActivity(), ExercisesActivity.class);
        intent.putExtra(SELECTED_MUSCLE, LOWERLEG);
        startActivity(intent);
    }

    @OnClick(R.id.showall)
    void qqqqqqqqqqqq() {
        Intent intent = new Intent(getActivity(), ExercisesActivity.class);
        intent.putExtra(SELECTED_MUSCLE, SHOWALL);
        startActivity(intent);
    }




}
