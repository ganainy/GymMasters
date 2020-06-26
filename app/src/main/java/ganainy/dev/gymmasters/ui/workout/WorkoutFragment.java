package ganainy.dev.gymmasters.ui.workout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.Workout;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ganainy.dev.gymmasters.ui.main.ActivityCallback;
import ganainy.dev.gymmasters.utils.AuthUtils;
import ganainy.dev.gymmasters.utils.SharedPrefUtils;

import static ganainy.dev.gymmasters.utils.SharedPrefUtils.IS_FIRST_SHOWING_OF_WORKOUT;

public class WorkoutFragment extends Fragment {
    private static final String TAG = "SpecificWorkoutActivity";
    private static final String MY_PREFS_NAME = "mSharedPref";
    public static final String WORKOUT = "workout";
    private Workout workout;

    private WorkoutViewModel workoutViewModel;
    private SpecificWorkoutAdapter specificWorkoutAdapter;

    @BindView(R.id.workoutRecycler)
    RecyclerView workoutRecycler;

    @BindView(R.id.deleteImageView)
    ImageView deleteImageView;

    @BindView(R.id.workoutHintLayout)
    ConstraintLayout workoutHintLayout;

    @BindView(R.id.titleTextView)
    TextView titleTextView;

    @OnClick(R.id.backArrowImageView)
    void onBackArrowClick(){
        requireActivity().onBackPressed();
    }

    @OnClick(R.id.deleteImageView)
    void onDeleteImageClick(){
        new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.delete_workout_question)
                .setMessage(R.string.confirm_delete_workout)
                .setIcon(R.drawable.ic_delete_black_24dp)
                .setPositiveButton(R.string.delete, (dialog, which) -> deleteWorkout())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    public static WorkoutFragment newInstance(Workout workout){
        WorkoutFragment workoutFragment=new WorkoutFragment();
        Bundle bundle=new Bundle();
        bundle.putParcelable(WORKOUT,workout);
        workoutFragment.setArguments(bundle);
        return workoutFragment;
    }

    public WorkoutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.workout_fragment, container, false);
        ButterKnife.bind(this, view);
        setupRecycler();

        workoutHintLayout.findViewById(R.id.closeHintImageView).setOnClickListener(v->
                workoutHintLayout.setVisibility(View.INVISIBLE));

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //todo improve db design and only save exercise id+sets+reps or time instead of whole exercise

        workoutViewModel=new ViewModelProvider(this).get(WorkoutViewModel.class);

        if (getArguments().getParcelable(WORKOUT)!=null){
            workout=getArguments().getParcelable(WORKOUT);

            if (savedInstanceState==null){
                /*add two empty item in start and end of list to be replaces with start and ending dots*/
                workout.getWorkoutExerciseList().add(0,null);
                workout.getWorkoutExerciseList().add(null);
            }

            specificWorkoutAdapter.setData(workout.getWorkoutExerciseList());
            specificWorkoutAdapter.notifyDataSetChanged();
            showHintIfFirstViewedWorkout();

            titleTextView.setText(workout.getName());
            //only show delete fab if workout of logged in user(user coming from main fragment home)
            if (workout.getCreatorId().equals(AuthUtils.getLoggedUserId(requireContext()))) {
                deleteImageView.setVisibility(View.VISIBLE);
            }
            else {
                deleteImageView.setVisibility(View.GONE);
            }
        }

    }


    private void setupRecycler() {
         specificWorkoutAdapter = new SpecificWorkoutAdapter(requireActivity().getApplication(),
                new ExerciseInsideWorkoutCallback() {
                    @Override
                    public void onTimeExerciseClicked(Exercise exercise, Integer adapterPosition) {
                        openSelectedExerciseFragment(exercise);
                    }

                    @Override
                    public void onRepsExerciseClicked(Exercise exercise, Integer adapterPosition) {
                        openSelectedExerciseFragment(exercise);
                    }
                });

        workoutRecycler.setAdapter(specificWorkoutAdapter);
    }

    private void openSelectedExerciseFragment(Exercise exercise) {
        ((ActivityCallback)requireActivity()).openExerciseFragment(exercise);
    }



    private void deleteWorkout() {
        //remove exercise data from db
        final String workoutId = workout.getId();
        final String photoLink = workout.getPhotoLink();

        FirebaseDatabase.getInstance().getReference(WORKOUT).child(workoutId).setValue(null); //delete workout data
        FirebaseStorage.getInstance().getReference().child(photoLink).delete();//delete workout photo
        Toast.makeText(requireActivity(), R.string.deleted_successfully, Toast.LENGTH_SHORT).show();
        requireActivity().onBackPressed();
    }

    /**check if this is first time user viewing workout to show hint*/
    private void showHintIfFirstViewedWorkout() {
        Boolean isFirstShowingForWorkout = SharedPrefUtils.getBoolean(requireContext(),
                IS_FIRST_SHOWING_OF_WORKOUT);

        if (isFirstShowingForWorkout) return;

        workoutHintLayout.setVisibility(View.VISIBLE);
        SharedPrefUtils.putBoolean(requireContext(), false, IS_FIRST_SHOWING_OF_WORKOUT);
    }

}
