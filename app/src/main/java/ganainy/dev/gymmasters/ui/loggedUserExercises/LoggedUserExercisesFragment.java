package ganainy.dev.gymmasters.ui.loggedUserExercises;

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
import ganainy.dev.gymmasters.shared_adapters.ExercisesAdapter;
import ganainy.dev.gymmasters.ui.specificExercise.SpecificExerciseActivity;
import ganainy.dev.gymmasters.utils.NetworkState;

import static ganainy.dev.gymmasters.ui.exercise.ExercisesActivity.EXERCISE;
import static ganainy.dev.gymmasters.ui.main.home.HomeFragment.LOGGED_USER_ID;


public class LoggedUserExercisesFragment extends Fragment {

    @BindView(R.id.loading_layout_shimmer)
    ShimmerFrameLayout shimmerLoadingLayout;
    @BindView(R.id.empty_layout)
    ConstraintLayout emptyLayout;
    @BindView(R.id.error_layout)
    ConstraintLayout errorLayout;
    @BindView(R.id.exercisesRecyclerView)
    RecyclerView exercisesRecyclerView;

    @OnClick(R.id.backArrowImageView)
    void onBackArrowClick(){
        requireActivity().onBackPressed();
    }

    private ExercisesAdapter exercisesAdapter;
    private LoggedUserExercisesViewModel mViewModel;

    public static LoggedUserExercisesFragment newInstance(String loggedUserId) {
        LoggedUserExercisesFragment loggedUserExercisesFragment = new LoggedUserExercisesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LOGGED_USER_ID, loggedUserId);
        loggedUserExercisesFragment.setArguments(bundle);
        return loggedUserExercisesFragment;
    }

    private LoggedUserExercisesFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.logged_user_exercises_fragment, container, false);
        ButterKnife.bind(this,view);
        setupRecycler();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoggedUserExercisesViewModel.class);

        /*arguments can't be null since its passed with fragment instantiation*/
        mViewModel.downloadLoggedUserExercises(getArguments().getString(LOGGED_USER_ID));

        mViewModel.getExerciseListLiveData().observe(getViewLifecycleOwner(), exercises -> {
            exercisesAdapter.setData(exercises);
            exercisesAdapter.notifyDataSetChanged();
        });

        mViewModel.getNetworkStateLiveData().observe(getViewLifecycleOwner(), this::handleNetworkStateUi);
    }


    private void setupRecycler() {
        exercisesAdapter = new ExercisesAdapter(requireActivity().getApplicationContext(), exercise -> {
            //handle click of certain exercise
            Intent intent = new Intent(requireActivity(), SpecificExerciseActivity.class);
            intent.putExtra(EXERCISE, exercise);
            startActivity(intent);
        });
        exercisesRecyclerView.setAdapter(exercisesAdapter);
    }

    private void handleNetworkStateUi(NetworkState networkState) {
        switch (networkState) {
            case SUCCESS:
                errorLayout.setVisibility(View.INVISIBLE);
                emptyLayout.setVisibility(View.INVISIBLE);
                exercisesRecyclerView.setVisibility(View.VISIBLE);
                shimmerLoadingLayout.setVisibility(View.INVISIBLE);
                break;
            case ERROR:
                errorLayout.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.INVISIBLE);
                exercisesRecyclerView.setVisibility(View.INVISIBLE);
                shimmerLoadingLayout.setVisibility(View.INVISIBLE);
                break;
            case LOADING:
                errorLayout.setVisibility(View.INVISIBLE);
                emptyLayout.setVisibility(View.INVISIBLE);
                exercisesRecyclerView.setVisibility(View.INVISIBLE);
                shimmerLoadingLayout.setVisibility(View.VISIBLE);
                break;
            case EMPTY:
                errorLayout.setVisibility(View.INVISIBLE);
                emptyLayout.setVisibility(View.VISIBLE);
                exercisesRecyclerView.setVisibility(View.INVISIBLE);
                shimmerLoadingLayout.setVisibility(View.INVISIBLE);
                break;
        }
    }


}