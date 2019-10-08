package com.example.myapplication.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.WorkoutAdapter;
import com.example.myapplication.model.Workout;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragmentWorkouts extends Fragment {

    private static final String TAG = "MainFragmentWorkouts";
    private View view;
    private WorkoutAdapter workoutAdapter;
    ConstraintLayout loading_workouts;
    private MainFragmentWorkoutsViewModel mainFragmentWorkoutsViewModel;

    public MainFragmentWorkouts() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_fragment_workouts, container, false);
        this.view = view;

        /**loading layout to show while workouts are loading*/
        loading_workouts = view.findViewById(R.id.loading_workouts);


        mainFragmentWorkoutsViewModel = ViewModelProviders.of(this).get(MainFragmentWorkoutsViewModel.class);
        mainFragmentWorkoutsViewModel.downloadWorkout().observe(this, new Observer<List<Workout>>() {
            @Override
            public void onChanged(@Nullable List<Workout> workouts) {
                setupRecycler(workouts);
            }
        });

        return view;
    }


    private void setupRecycler(List<Workout> workouts) {
        loading_workouts.setVisibility(View.GONE);
        RecyclerView recyclerView = view.findViewById(R.id.workoutRecyclerView);
        workoutAdapter = new WorkoutAdapter(getActivity(), "fragmentWorkouts");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        workoutAdapter.setDataSource(workouts);
        recyclerView.setAdapter(workoutAdapter);
    }


}
