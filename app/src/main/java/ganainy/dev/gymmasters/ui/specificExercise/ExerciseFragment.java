package ganainy.dev.gymmasters.ui.specificExercise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.ui.main.ActivityCallback;

public class ExerciseFragment extends LogFragment  {

    public static final String ISOLATED = "isolated";
    public static final String COMPOUND = "compound";
    public static final String EXERCISE = "exercise";

    private ExerciseViewModel mViewModel;

    @BindView(R.id.workoutImageView)
    ImageView exerciseImageView;

    @BindView(R.id.nameTextView)
    TextView nameTextView;

    @BindView(R.id.executionTextView)
    TextView executionTextView;

    @BindView(R.id.additionalNotesTextView)
    TextView additionalNotesTextView;

    @BindView(R.id.mechanicTextView)
    TextView mechanicTextView;

    @BindView(R.id.targetedMuscleTextView)
    TextView targetedMuscleTextView;

    @BindView(R.id.numOneEditText)
    TextView numOneEditText;

    @BindView(R.id.numTwoEditText)
    TextView numTwoEditText;

    @BindView(R.id.titleTextView)
    TextView titleTextView;

    @BindView(R.id.deleteImageView)
    ImageView deleteImageView;

    @BindView(R.id.exercise_deleted_layout)
    ConstraintLayout exerciseDeletedLayout;

    @BindView(R.id.showVideoButton)
    Button showVideoButton;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @OnClick(R.id.backArrowImageView)
    void onBackArrowClick() {
        requireActivity().onBackPressed();
    }

    @OnClick(R.id.showVideoButton)
    void switchToVideoButton() {
        openYoutubeFragment();
    }

    private void openYoutubeFragment() {
        ActivityCallback activityCallback = (ActivityCallback) requireActivity();
        activityCallback.openYoutubeFragment(mViewModel.getExercise().getName());
    }

    @OnClick(R.id.mechanicQuestionMark)
    void showMechanicInfo() {
        showMechanicInfoDialog(mechanicTextView.getText().toString().toLowerCase());
    }

    @OnClick(R.id.deleteImageView)
    public void onViewClicked() {
        showConfirmDeleteDialog();
    }

    private void showConfirmDeleteDialog() {
        new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.delete_exercise_q)
                .setMessage(R.string.confirm_delete_exercise)
                .setIcon(R.drawable.ic_delete_black_24dp)
                .setPositiveButton(R.string.delete, (dialog, which) -> mViewModel.deleteExercise())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }



    public static ExerciseFragment newInstance(Exercise exercise) {
        ExerciseFragment exerciseFragment = new ExerciseFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXERCISE, exercise);
        exerciseFragment.setArguments(bundle);
        return exerciseFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.exercise_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViewModel();

        if (getArguments().getParcelable(EXERCISE) != null) {
            Exercise exercise = getArguments().getParcelable(EXERCISE);
            showExerciseInUi(exercise);
            mViewModel.setExercise(exercise);
            mViewModel.loadExercisePhotos();
            mViewModel.isLoggedUserExercise();
        }


        /*only show delete button if this exercise is owned by logged in user*/
        mViewModel.getIsLoggedUserExerciseLiveData().observe(getViewLifecycleOwner(), isLoggedUserExercise -> {
            if (isLoggedUserExercise) deleteImageView.setVisibility(View.VISIBLE);
            else deleteImageView.setVisibility(View.GONE);
        });

        /*show/hide photo/youtube player based on user choice*/
        mViewModel.getExerciseSelectedImageLiveData().observe(getViewLifecycleOwner(), exerciseViewType -> {
            switch (exerciseViewType){
                case IMAGE_ONE:
                    showFirstPhoto();
                    break;
                case IMAGE_TWO:
                    showSecondPhoto();
                    break;
            }
        });

        mViewModel.getIsExerciseDeletedSuccessfullyLiveData().observe(getViewLifecycleOwner(), isExerciseDeletedSuccessfully -> {
            if (isExerciseDeletedSuccessfully) {
                Toast.makeText(requireActivity(), R.string.exercise_deleted_successfully, Toast.LENGTH_LONG).show();
                requireActivity().onBackPressed();
            } else {
                Toast.makeText(requireActivity(), R.string.error_deleting_exercise, Toast.LENGTH_SHORT).show();
            }
        });
            }

    private void showSecondPhoto() {
        progressBar.setVisibility(View.GONE);
        numOneEditText.setVisibility(View.GONE);
        numTwoEditText.setVisibility(View.VISIBLE);
        exerciseImageView.setImageDrawable(mViewModel.getSecondDrawable());
    }

    private void initViewModel() {
        mViewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);
    }

    private void showExerciseInUi(Exercise exercise) {
        nameTextView.setText(exercise.getName());
        titleTextView.setText(exercise.getName());
        executionTextView.setText(exercise.getExecution());
        mechanicTextView.setText(exercise.getMechanism());
        targetedMuscleTextView.setText(exercise.getBodyPart());
        if (exercise.getAdditional_notes() == null) additionalNotesTextView.setText(R.string.none);
        else additionalNotesTextView.setText(exercise.getAdditional_notes());
    }


    private void showFirstPhoto() {
        progressBar.setVisibility(View.GONE);
        numOneEditText.setVisibility(View.VISIBLE);
        numTwoEditText.setVisibility(View.GONE);
        exerciseImageView.setImageDrawable(mViewModel.getFirstDrawable());
    }




    private void showMechanicInfoDialog(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setCancelable(false)
                .setPositiveButton(R.string.got_it, (dialogInterface, i) -> {
                    //Do nothing
                });

        //change message depending on type of mechanism , utility
        switch (s) {
            case ISOLATED: {
                builder.setMessage(
                        getString(R.string.isolated_definition));
                break;
            }
            case COMPOUND: {
                builder.setMessage(R.string.compound_definition);
                break;
            }
        }
        builder.create().show();
    }


}