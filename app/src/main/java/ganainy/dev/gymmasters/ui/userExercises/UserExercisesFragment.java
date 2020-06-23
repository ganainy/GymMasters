package ganainy.dev.gymmasters.ui.userExercises;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.shared_adapters.ExercisesAdapter;
import ganainy.dev.gymmasters.ui.main.ActivityCallback;
import ganainy.dev.gymmasters.utils.NetworkState;

import static ganainy.dev.gymmasters.ui.main.home.HomeFragment.USER_ID;

/**can be called to show logged user exercises or any user exercises*/
public class UserExercisesFragment extends Fragment {

    public static final String USER_NAME = "userName";
    @BindView(R.id.loading_layout_shimmer)
    ShimmerFrameLayout shimmerLoadingLayout;
    @BindView(R.id.empty_layout)
    ConstraintLayout emptyLayout;
    @BindView(R.id.error_layout)
    ConstraintLayout errorLayout;
    @BindView(R.id.exercisesRecyclerView)
    RecyclerView exercisesRecyclerView;
    @BindView(R.id.toolbarTitleTextView)
    TextView toolbarTitleTextView;

    @OnClick(R.id.backArrowImageView)
    void onBackArrowClick(){
        requireActivity().onBackPressed();
    }

    private ExercisesAdapter exercisesAdapter;
    private UserExercisesViewModel mViewModel;

    public static UserExercisesFragment newInstance(String userId,String userName) {
        UserExercisesFragment userExercisesFragment = new UserExercisesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userId);
        bundle.putString(USER_NAME, userName);
        userExercisesFragment.setArguments(bundle);
        return userExercisesFragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.user_exercises_fragment, container, false);
        ButterKnife.bind(this,view);
        setupRecycler();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(UserExercisesViewModel.class);

        /*arguments can't be null since its passed with fragment instantiation*/
        mViewModel.downloadLoggedUserExercises(getArguments().getString(USER_ID));

        setToolbarTitle(getArguments().getString(USER_NAME));

        mViewModel.getExerciseListLiveData().observe(getViewLifecycleOwner(), exercises -> {
            exercisesAdapter.setData(exercises);
            exercisesAdapter.notifyDataSetChanged();
        });

        mViewModel.getNetworkStateLiveData().observe(getViewLifecycleOwner(), this::handleNetworkStateUi);
    }

    private void setToolbarTitle(String userName) {
        if (userName!=null)
        toolbarTitleTextView.setText(userName+"'s exercises");
    }


    private void setupRecycler() {
        exercisesAdapter = new ExercisesAdapter(requireActivity().getApplicationContext(), exercise -> {
            //handle click of certain exercise
            ActivityCallback activityCallback = (ActivityCallback) requireActivity();
            activityCallback.openExerciseFragment(exercise);
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