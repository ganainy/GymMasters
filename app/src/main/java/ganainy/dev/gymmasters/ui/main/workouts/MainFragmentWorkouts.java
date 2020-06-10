package ganainy.dev.gymmasters.ui.main.workouts;


import android.os.Bundle;
import android.util.Log;
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

import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.shared_adapters.WorkoutAdapter;
import ganainy.dev.gymmasters.models.app_models.Workout;

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
    private RecyclerView recyclerView;

    public MainFragmentWorkouts() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_fragment_workouts, container, false);
        this.view = view;
        recyclerView = view.findViewById(R.id.workoutRecyclerView);


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
        workoutAdapter = new WorkoutAdapter(getActivity(), "fragmentWorkouts");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        workoutAdapter.setDataSource(workouts);
        recyclerView.setAdapter(workoutAdapter);
    }


    @Override
    public void onStop() {
        super.onStop();
        recyclerView.setAdapter(null);
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        if (workoutAdapter != null) recyclerView.setAdapter(workoutAdapter);
    }


}
