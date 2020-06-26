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
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.shared_adapters.WorkoutAdapter;
import ganainy.dev.gymmasters.ui.main.ActivityCallback;
import ganainy.dev.gymmasters.ui.workout.WorkoutFragment;
import ganainy.dev.gymmasters.utils.NetworkState;

import static ganainy.dev.gymmasters.ui.main.home.ProfileFragment.USER_ID;
import static ganainy.dev.gymmasters.ui.main.workouts.WorkoutsFragment.WORKOUT;

public class UserWorkoutsFragment extends Fragment {

    public static final String USER_NAME = "userName";
    private UserWorkoutsViewModel mViewModel;
    private WorkoutAdapter workoutAdapter;
    @BindView(R.id.workoutRecyclerView)
     RecyclerView recyclerView;
    @BindView(R.id.loading_layout_shimmer)
    LinearLayout shimmerLoadingLayout;
    @BindView(R.id.empty_layout)
    ConstraintLayout emptyLayout;
    @BindView(R.id.error_layout)
    ConstraintLayout errorLayout;
    @BindView(R.id.toolbarTitleTextView)
    TextView toolbarTitleTextView;


    @OnClick(R.id.backArrowImageView)
    void onBackArrowClick(){
        requireActivity().onBackPressed();
    }

    public static UserWorkoutsFragment newInstance(String userId,String userName) {
        UserWorkoutsFragment userWorkoutsFragment = new UserWorkoutsFragment();
        Bundle bundle=new Bundle();
        bundle.putString(USER_ID,userId);
        bundle.putString(USER_NAME,userName);
        userWorkoutsFragment.setArguments(bundle);
        return userWorkoutsFragment;
    }

    public UserWorkoutsFragment() {

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
        mViewModel = new ViewModelProvider(this).get(UserWorkoutsViewModel.class);
        mViewModel.downloadLoggedUserWorkouts(getArguments().getString(USER_ID));

        setupToolbarTitle(getArguments().getString(USER_NAME));

        mViewModel.getWorkoutListLiveData().observe(getViewLifecycleOwner(), workouts -> {
            workoutAdapter.setDataSource(workouts);
            workoutAdapter.notifyDataSetChanged();
        });

        mViewModel.getNetworkStateLiveData().observe(getViewLifecycleOwner(), this::handleNetworkStateUi);

    }

    private void setupToolbarTitle(String username) {
        if (username!=null)
            toolbarTitleTextView.setText(username+"'s workouts");
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
            ((ActivityCallback)requireActivity()).onOpenWorkoutFragment(clickedWorkout);
        });
        recyclerView.setAdapter(workoutAdapter);
    }
}