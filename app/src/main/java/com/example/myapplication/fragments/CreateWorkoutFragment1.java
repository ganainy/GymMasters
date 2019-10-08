package com.example.myapplication.fragments;


import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.model.Workout;
import com.example.myapplication.ui.MainActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shashank.sony.fancytoastlib.FancyToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateWorkoutFragment1 extends Fragment {
    private static final String TAG = "CreateWorkoutFragment1";
    @BindView(R.id.nameEditText)
    EditText nameEditText;

    @BindView(R.id.durationEditText)
    EditText durationEditText;

    @BindView(R.id.levelSpinner)
    Spinner levelSpinner;

    @BindView(R.id.addExercisePhoto)
    ImageView addExercisePhoto;


    private String newWorkoutLevel;
    private Uri imageUri;
    private MainActivity mainActivity;


    public CreateWorkoutFragment1() {
        // Required empty public constructor
    }

    @OnClick(R.id.addExercisePhoto)
    void getPhotoAndShow() {
        mainActivity.getPhotoFromGallery();

        mainActivity.setOnBundleSelected(new MainActivity.SelectedBundle() {
            @Override
            public void onBundleSelect(Bundle bundle) {
                Log.i(TAG, "onBundleSelect1: ");
                String imageString = (String) bundle.get("imageString");
                imageUri = Uri.parse(imageString);
                addExercisePhoto.setImageURI(imageUri);
            }
        });

    }

    @OnClick(R.id.nextButton)
    void gotoNextFragment() {

        if (validateInputs()) {
            uploadWorkoutImage();
            saveDataInNewWorkout();
            mainActivity.gotoNextTab();
        }

    }

    private void saveDataInNewWorkout() {
        Workout workout = new Workout();
        workout.setName(nameEditText.getText().toString());
        workout.setDuration(durationEditText.getText().toString());
        workout.setLevel(newWorkoutLevel);
        workout.setPhotoLink("workoutImages/" + imageUri.getLastPathSegment());

        //now need to give this workout to mainactivity so it can pass it to the createWorkoutFragment2
        mainActivity.setWorkout(workout);
    }

    private void uploadWorkoutImage() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference imagesRef = storageRef.child("workoutImages/" + imageUri.getLastPathSegment());
        imagesRef.putFile(imageUri);
    }

    private boolean validateInputs() {
        if (nameEditText.getText().length() < 6) {
            FancyToast.makeText(getActivity(), "Name must be at least 6 chars.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;
        } else if (!checkInRange(durationEditText.getText().toString())) {
            FancyToast.makeText(getActivity(), "Duration must be from 0 to 120 minutes.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;
        } else if (imageUri == null) {
            FancyToast.makeText(getActivity(), "Select image to represent workout..", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;
        } else {

            return true;
        }
    }

    private boolean checkInRange(String text) {
        return Integer.valueOf(text) > 0 && Integer.valueOf(text) < 120;
    }

    private void levelSpinnerCode() {
        final String[] level = {"Beginner", "Intermediate", "Professional"};
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
        View view = inflater.inflate(R.layout.fragment_create_workout_fragment1, container, false);
        ButterKnife.bind(this, view);
        mainActivity = (MainActivity) getActivity();
        levelSpinnerCode();
        return view;
    }


}
