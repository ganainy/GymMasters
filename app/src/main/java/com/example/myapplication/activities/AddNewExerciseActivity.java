package com.example.myapplication.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.myapplication.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddNewExerciseActivity extends AppCompatActivity {
    String newExerciseSelectedMuscle;


    @BindView(R.id.bodyPartSpinner)
    Spinner bodyPartSpinner;

/*@BindView(R.id.saveButton)
    Button saveButton;*/

    @OnClick(R.id.saveButton)
    void saveExercise() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_exercise);
        ButterKnife.bind(this);

        muscleSpinnerCode();
    }

    private void muscleSpinnerCode() {
        final String[] muscles = {"Chest", "Triceps", "Shoulders", "Biceps", "Abs", "Back", "Forearm", "Upper leg", "Glutes", "Cardio", "Lower leg", "Other"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, muscles);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        bodyPartSpinner.setAdapter(arrayAdapter);
        bodyPartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                newExerciseSelectedMuscle = muscles[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
