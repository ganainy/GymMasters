package ganainy.dev.gymmasters.ui.main.workouts;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.shared_adapters.WorkoutAdapter;
import ganainy.dev.gymmasters.ui.main.ActivityCallback;
import ganainy.dev.gymmasters.utils.NetworkState;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkoutsFragment extends Fragment {

    private static final String TAG = "MainFragmentWorkouts";
    public static final String WORKOUT = "workout";
    private WorkoutAdapter workoutAdapter;
    private WorkoutsViewModel workoutsViewModel;
    private RecyclerView recyclerView;

    @BindView(R.id.loading_layout_shimmer)
    LinearLayout loading_layout_shimmer;

    @BindView(R.id.empty_layout)
    ConstraintLayout emptyLayout;

    @BindView(R.id.error_layout)
    ConstraintLayout errorLayout;

    public WorkoutsFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new WorkoutsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workouts, container, false);
        ButterKnife.bind(this, view);
        setupRecycler(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        workoutsViewModel = new ViewModelProvider(this).get(WorkoutsViewModel.class);

        workoutsViewModel.getWorkoutListLiveData().observe(getViewLifecycleOwner(), workouts -> {
            workoutAdapter.setDataSource(workouts);
            workoutAdapter.notifyDataSetChanged();
        });

        workoutsViewModel.getNetworkStateLiveData().observe(getViewLifecycleOwner(), this::handleNetworkStateUi);
    }

    private void handleNetworkStateUi(NetworkState networkState) {
        switch (networkState){
            case SUCCESS:
                errorLayout.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                loading_layout_shimmer.setVisibility(View.GONE);
                break;
            case ERROR:
                errorLayout.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                loading_layout_shimmer.setVisibility(View.GONE);
                break;
            case LOADING:
                errorLayout.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                loading_layout_shimmer.setVisibility(View.VISIBLE);
                break;
            case EMPTY:
                errorLayout.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                loading_layout_shimmer.setVisibility(View.GONE);
                break;
        }
    }


    private void setupRecycler(View view) {
        recyclerView = view.findViewById(R.id.workoutRecyclerView);
        workoutAdapter = new WorkoutAdapter(requireContext().getApplicationContext(), clickedWorkout -> {
            ((ActivityCallback)requireActivity()).onOpenWorkoutFragment(clickedWorkout);
        });
        recyclerView.setAdapter(workoutAdapter);
    }
}
