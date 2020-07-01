package ganainy.dev.gymmasters.ui.muscle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.shared_adapters.ExercisesAdapter;
import ganainy.dev.gymmasters.ui.main.ActivityCallback;
import ganainy.dev.gymmasters.utils.MiscellaneousUtils;
import ganainy.dev.gymmasters.utils.NetworkState;

import static ganainy.dev.gymmasters.ui.main.exercisesCategories.ExercisesCategoriesFragment.SELECTED_MUSCLE;

public class MuscleFragment extends Fragment {
    private static final String TAG = "ExercisesActivity";
    public static final String EXERCISE = "exercise";
    MuscleViewModel muscleViewModel;

    @BindView(R.id.collapse_toolbar)
    CollapsingToolbarLayout htab_collapse_toolbar;
    @BindView(R.id.htab_header)
    ImageView collapsingToolbarImage;
    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.loading_layout_shimmer)
    LinearLayout shimmerLoadingLayout;
    @BindView(R.id.empty_layout)
    ConstraintLayout emptyLayout;
    @BindView(R.id.error_layout)
    ConstraintLayout errorLayout;
    @BindView(R.id.exercisesRecyclerView)
    RecyclerView exercisesRecyclerView;


    private ExercisesAdapter exercisesAdapter;

    public static MuscleFragment newInstance(String selectedMuscle){
        MuscleFragment muscleFragment=new MuscleFragment();
        Bundle bundle=new Bundle();
        bundle.putString(SELECTED_MUSCLE,selectedMuscle);
        muscleFragment.setArguments(bundle);
        return muscleFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.muscle_fragment, container, false);
        ButterKnife.bind(this, view);
        setupRecycler();
        setupSearchView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        muscleViewModel = new ViewModelProvider(this).get(MuscleViewModel.class);

        if (getArguments().getString(SELECTED_MUSCLE)!=null){
            String selectedMuscle = getArguments().getString(SELECTED_MUSCLE);
            setTabHeaderImage(selectedMuscle);
            muscleViewModel.getSelectedMuscleExercises(selectedMuscle);
        }

        muscleViewModel.getExerciseListLiveData().observe(getViewLifecycleOwner(), exercises -> {
            exercisesAdapter.setData(exercises);
            exercisesAdapter.notifyDataSetChanged();
        });

        muscleViewModel.getNetworkStateLiveData().observe(getViewLifecycleOwner(), this::handleNetworkStateUi);
    }



    private void handleNetworkStateUi(NetworkState networkState) {
        switch (networkState){
            case SUCCESS:
                errorLayout.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.GONE);
                exercisesRecyclerView.setVisibility(View.VISIBLE);
                shimmerLoadingLayout.setVisibility(View.GONE);
                break;
            case ERROR:
                errorLayout.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);
                exercisesRecyclerView.setVisibility(View.GONE);
                shimmerLoadingLayout.setVisibility(View.GONE);
                break;
            case LOADING:
                errorLayout.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.GONE);
                exercisesRecyclerView.setVisibility(View.GONE);
                shimmerLoadingLayout.setVisibility(View.VISIBLE);
                break;
            case EMPTY:
                errorLayout.setVisibility(View.GONE);
                 emptyLayout.setVisibility(View.VISIBLE);
                exercisesRecyclerView.setVisibility(View.GONE);
                shimmerLoadingLayout.setVisibility(View.GONE);
                break;
        }
    }


    /**
     * set header image based on selected muscle from previous fragment
     */
    private void setTabHeaderImage(String selectedMuscle) {
        collapsingToolbarImage.setImageResource(MiscellaneousUtils.getImageId(requireActivity(), selectedMuscle));
    }

    private void setupRecycler() {

        //handle click of certain exercise
        exercisesAdapter = new ExercisesAdapter(requireActivity(), clickedExercise -> {
            ((ActivityCallback) requireActivity()).openExerciseFragment(clickedExercise);
        });
        exercisesRecyclerView.setAdapter(exercisesAdapter);
    }

    private void setupSearchView() {
        //do filtering when i type in search or click search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryString) {
                exercisesAdapter.getFilter().filter(queryString);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String queryString) {
                exercisesAdapter.getFilter().filter(queryString);
                return false;
            }
        });
    }

}
