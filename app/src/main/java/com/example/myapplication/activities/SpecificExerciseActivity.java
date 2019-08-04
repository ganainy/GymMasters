package com.example.myapplication.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.myapplication.R;
import com.example.myapplication.model.Exercise;

public class SpecificExerciseActivity extends AppCompatActivity {

    private static final String TAG ="SpecificExerciseActivit" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_exercise);


        Intent i = getIntent();
        Exercise exercise = (Exercise)i.getSerializableExtra("exercise");
     //TODO show exercise info in proper layout

    }
}
