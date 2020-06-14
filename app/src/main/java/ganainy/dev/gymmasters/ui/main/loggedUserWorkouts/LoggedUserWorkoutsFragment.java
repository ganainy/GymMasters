package ganainy.dev.gymmasters.ui.main.loggedUserWorkouts;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.shared_adapters.WorkoutAdapter;
import ganainy.dev.gymmasters.ui.specificWorkout.SpecificWorkoutActivity;
import ganainy.dev.gymmasters.utils.NetworkState;

import static ganainy.dev.gymmasters.ui.main.home.HomeFragment.LOGGED_USER_ID;
import static ganainy.dev.gymmasters.ui.main.workouts.WorkoutsFragment.WORKOUT;

public class LoggedUserWorkoutsFragment extends Fragment {

    private LoggedUserWorkoutsViewModel mViewModel;
    private WorkoutAdapter workoutAdapter;
    @BindView(R.id.workoutRecyclerView)
     RecyclerView recyclerView;
    @BindView(R.id.loading_layout_shimmer)
    ShimmerFrameLayout shimmerLoadingLayout;
    @BindView(R.id.empty_layout)
    ConstraintLayout emptyLayout;
    @BindView(R.id.error_layout)
    ConstraintLayout errorLayout;


    @OnClick(R.id.backArrowImageView)
    void onBackArrowClick(){
        requireActivity().onBackPressed();
    }

    public static LoggedUserWorkoutsFragment newInstance(String loggedUserId) {
        LoggedUserWorkoutsFragment loggedUserWorkoutsFragment= new LoggedUserWorkoutsFragment();
        Bundle bundle=new Bundle();
        bundle.putString(LOGGED_USER_ID,loggedUserId);
        loggedUserWorkoutsFragment.setArguments(bundle);
        return loggedUserWorkoutsFragment;
    }

    private LoggedUserWorkoutsFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.logged_user_workouts_fragment, container, false);
        ButterKnife.bind(this, view);
        setupRecycler(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoggedUserWorkoutsViewModel.class);
        mViewModel.downloadLoggedUserWorkouts(getArguments().getString(LOGGED_USER_ID));

        mViewModel.getWorkoutListLiveData().observe(getViewLifecycleOwner(), workouts -> {
            workoutAdapter.setDataSource(workouts);
            workoutAdapter.notifyDataSetChanged();
        });

        mViewModel.getNetworkStateLiveData().observe(getViewLifecycleOwner(), this::handleNetworkStateUi);

    }

    private void handleNetworkStateUi(NetworkState networkState) {
        switch (networkState){
            case SUCCESS:
                errorLayout.setVisibility(View.INVISIBLE);
                emptyLayout.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                shimmerLoadingLayout.setVisibility(View.INVISIBLE);
                break;
            case ERROR:
                errorLayout.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
                shimmerLoadingLayout.setVisibility(View.INVISIBLE);
                break;
            case LOADING:
                errorLayout.setVisibility(View.INVISIBLE);
                emptyLayout.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
                shimmerLoadingLayout.setVisibility(View.VISIBLE);
                break;
            case EMPTY:
                errorLayout.setVisibility(View.INVISIBLE);
                emptyLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
                shimmerLoadingLayout.setVisibility(View.INVISIBLE);
                break;
        }
    }


    private void setupRecycler(View view) {
        recyclerView = view.findViewById(R.id.workoutRecyclerView);
        workoutAdapter = new WorkoutAdapter(requireContext().getApplicationContext(), clickedWorkout -> {
            Intent intent = new Intent(requireContext(), SpecificWorkoutActivity.class);
            intent.putExtra(WORKOUT, clickedWorkout);
            startActivity(intent);
        });
        recyclerView.setAdapter(workoutAdapter);
    }
}