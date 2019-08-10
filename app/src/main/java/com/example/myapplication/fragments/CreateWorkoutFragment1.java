package com.example.myapplication.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.myapplication.R;
import com.example.myapplication.activities.MainActivity;
import com.shashank.sony.fancytoastlib.FancyToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateWorkoutFragment1 extends Fragment {

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


    public CreateWorkoutFragment1() {
        // Required empty public constructor
    }

    @OnClick(R.id.addExercisePhoto)
    void getPhotoFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 103);

        //
        ((MainActivity) getActivity()).setOnBundleSelected(new MainActivity.SelectedBundle() {
            @Override
            public void onBundleSelect(Bundle bundle) {
                String imageString = (String) bundle.get("imageString");
                imageUri = Uri.parse(imageString);
                addExercisePhoto.setImageURI(imageUri);
            }
        });
    }

    @OnClick(R.id.nextButton)
    void gotoNextFragment() {

        validateInputs();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.gotoNextTab();
    }

    private void validateInputs() {
        if (nameEditText.getText().length() < 6) {
            FancyToast.makeText(getActivity(), "Name must be at least 6 chars.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
        } else if (!checkInRange(durationEditText.getText().toString())) {
            FancyToast.makeText(getActivity(), "Duration must be from 0 to 120 minutes.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
        } else if (imageUri == null) {
            FancyToast.makeText(getActivity(), "Select image to represent workout..", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
        }
    }

    private boolean checkInRange(String text) {
        if (Integer.valueOf(text) > 0 && Integer.valueOf(text) < 120)
            return true;
        else
            return false;
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
        levelSpinnerCode();
        return view;
    }


}
