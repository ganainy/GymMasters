package ganainy.dev.gymmasters.ui.createWorkout;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.Workout;
import ganainy.dev.gymmasters.ui.main.MainActivity;
import ganainy.dev.gymmasters.utils.AuthUtils;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static ganainy.dev.gymmasters.ui.exercise.ExercisesViewModel.EXERCISES;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateWorkoutFragment extends Fragment {
    private static final String TAG = "CreateWorkoutFragment";
    @BindView(R.id.nameEditText)
    EditText nameEditText;

    @BindView(R.id.durationEditText)
    EditText durationEditText;

    @BindView(R.id.levelSpinner)
    Spinner levelSpinner;

    @BindView(R.id.workoutImageView)
    ImageView workoutImage;


    @BindView(R.id.exercisesRecycler)
    RecyclerView exercisesRecycler;
    @BindView(R.id.searchView)
    EditText searchView;
    @BindView(R.id.circle_progress)
    CircleProgress circleProgress;


    private String newWorkoutLevel;
    private Uri imageUri;
    private List<Exercise> exerciseList;
    private ExerciseAdapterAdvanced exerciseAdapter;
    private List<Exercise> exercisesOfWorkoutList;

    private ConstraintLayout loadingLayout;
    private int fakeProgress;


    public CreateWorkoutFragment() {
        // Required empty public constructor
    }


    private void uploadWorkoutImage() {

        /**this list contains exercises i added to the work out and each one has sets and reps*/
        exercisesOfWorkoutList = exerciseAdapter.getExercisesOfWorkoutList();

        if (!validateInputs()) {
            return;
        }


        /**unique number to attach to image path */
        Date date = new Date();
        final long timeMilli = date.getTime();

        /**show loading layout*/
        loadingLayout.setVisibility(View.VISIBLE);


        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference imagesRef = storageRef.child("workoutImages/" + imageUri.getLastPathSegment() + timeMilli);
        imagesRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(TAG, "isAdded: " + isAdded() + isVisible() + getUserVisibleHint());
                if (isAdded() && isVisible() && getUserVisibleHint()) {
                    //check if fragment is visible
                    uploadWorkout(timeMilli);
                } else {
                    //delete uploaded photo since workout wont be uploaded
                    imagesRef.delete();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingLayout.setVisibility(View.GONE);
                Log.i(TAG, "onFailure: " + e.getMessage());
                FancyToast.makeText(getActivity(), "Uploading failed , check connection and try again", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                //calculating progress percentage
                final double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //displaying percentage in circleLoadingView


                //add little delay when updating progress to look smoother
                new Thread() {
                    public void run() {
                        while (fakeProgress < (int) progress) {
                            try {
                                getActivity().runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        fakeProgress++;
                                        circleProgress.setProgress(fakeProgress);
                                    }
                                });
                                Thread.sleep(15);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();

                /* circleProgress.setProgress((int) progress);*/
            }
        });
    }

    private boolean validateInputs() {
        if (nameEditText.getText().length() < 6) {
            FancyToast.makeText(getActivity(), "Name must be at least 6 chars", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;
        } else if (!checkInRange(durationEditText.getText().toString())) {
            FancyToast.makeText(getActivity(), "Duration must be from 0 to 120 minutes", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;
        } else if (imageUri == null) {
            FancyToast.makeText(getActivity(), "Select image to represent workout", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;
        } else if (newWorkoutLevel.equals("")) {
            FancyToast.makeText(getActivity(), "Select workout level", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;
        } else if (exercisesOfWorkoutList == null || exercisesOfWorkoutList.size() == 0) {
            FancyToast.makeText(getActivity(), "Workout must have at lease 1 exercise", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;
        } else {

            return true;
        }
    }

    private boolean checkInRange(String text) {
        return Integer.valueOf(text) > 0 && Integer.valueOf(text) <= 120;
    }

    private void levelSpinnerCode() {
        final String[] level = {"", "Beginner", "Intermediate", "Professional"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, level);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        levelSpinner.setAdapter(arrayAdapter);
        levelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                newWorkoutLevel = level[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.create_workout_fragment, container, false);
        loadingLayout = view.findViewById(R.id.loading_layout);
        // setHasOptionsMenu(true);
        ButterKnife.bind(this, view);

        levelSpinnerCode();
        downloadAllExercises();
        return view;
    }


    private void downloadAllExercises() {
        exerciseList = new ArrayList<>();
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Exercise exercise = new Exercise();
                    exercise.setName(ds.child("name").getValue().toString());
                    exercise.setBodyPart(ds.child("bodyPart").getValue().toString());
                    exercise.setPreviewPhoto1(ds.child("previewPhoto1").getValue().toString());
                    exerciseList.add(exercise);

                }

                //downloadExercisesImages();
                setupRecycler();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        FirebaseDatabase.getInstance().getReference(EXERCISES).addChildEventListener(childEventListener);

    }

    private void setupRecycler() {

        exerciseAdapter = new ExerciseAdapterAdvanced(getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        exerciseAdapter.setDataSource(exerciseList);
        exercisesRecycler.setLayoutManager(linearLayoutManager);
        exercisesRecycler.setAdapter(exerciseAdapter);

        addSearchFunctionality();
    }

    private void addSearchFunctionality() {
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i(TAG, "onQueryTextChange: " + editable);
                //so app won't crash if no data in recycler
                if (exerciseAdapter != null)
                    exerciseAdapter.getFilter().filter(editable);
            }
        });
    }

    private void uploadWorkout(long timeMilli) {


        DatabaseReference workoutRef = FirebaseDatabase.getInstance().getReference("workout");


        Workout workout = new Workout();
        workout.setName(nameEditText.getText().toString());
        workout.setDuration(durationEditText.getText().toString());
        workout.setLevel(newWorkoutLevel);
        workout.setPhotoLink("workoutImages/" + imageUri.getLastPathSegment() + timeMilli);
        workout.setCreatorId(AuthUtils.getLoggedUserId(requireContext()));
        /**save user id and date with workout*/
        String id = workoutRef.push().getKey();
        workout.setId(id);
        workout.setDate(String.valueOf(System.currentTimeMillis()));

        workout.setExercisesNumber(String.valueOf(exercisesOfWorkoutList.size()));
        workout.setWorkoutExerciseList(exercisesOfWorkoutList);

        workoutRef.child(id).setValue(workout).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loadingLayout.setVisibility(View.GONE);
                startActivity(new Intent(getActivity(), MainActivity.class));
                FancyToast.makeText(getActivity(), "Workout uploaded", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                FancyToast.makeText(getActivity(), "Uploading failed , check connection and retry", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                loadingLayout.setVisibility(View.GONE);
            }
        });
    }


    @OnClick(R.id.workoutImageView)
    void openGalleryImageChooser() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 103);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        if (requestCode == 103) {
            Log.i(TAG, "requestCode: ok");
            imageUri =data.getData();
            workoutImage.setPadding(0, 0, 0, 0);
            workoutImage.setImageURI(imageUri);
        }
    }

    @OnClick(R.id.uploadButton)
    public void onuploadClicked() {
        /**upload workout image to storge*/
        uploadWorkoutImage();


    }


    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
        exercisesRecycler.setAdapter(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        exercisesRecycler.setAdapter(exerciseAdapter);
    }
}
