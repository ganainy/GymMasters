package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.SpecificMusclePagerAdapter;
import com.example.myapplication.model.Exercise;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExercisesActivity extends AppCompatActivity {
    private static final String TAG = "ExercisesActivity";
    ViewPager viewPager;
    String muscle;

    @BindView(R.id.htab_collapse_toolbar)
    CollapsingToolbarLayout htab_collapse_toolbar;

    @BindView(R.id.htab_header)
    ImageView htab_header;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);
        ButterKnife.bind(this);

        //set header image based on selected muscle from previous fragment
        setTabHeaderIamge();


        setupViewPager();
    }

    private void setTabHeaderIamge() {

        if (getIntent().hasExtra("triceps")) {
            htab_header.setImageResource(R.drawable.triceps);
            this.muscle = "triceps";
        } else if (getIntent().hasExtra("chest")) {
            htab_header.setImageResource(R.drawable.chest);
            this.muscle = "chest";
            Log.i(TAG, "setTabHeaderIamge: chest");
        } else if (getIntent().hasExtra("shoulders")) {
            htab_header.setImageResource(R.drawable.shoulder);
            this.muscle = "shoulders";
            Log.i(TAG, "setTabHeaderIamge: shoulders");
        } else if (getIntent().hasExtra("biceps")) {
            htab_header.setImageResource(R.drawable.biceps);
            this.muscle = "biceps";
            Log.i(TAG, "setTabHeaderIamge: biceps");
        } else if (getIntent().hasExtra("abs")) {
            htab_header.setImageResource(R.drawable.abs);
            this.muscle = "abs";
        } else if (getIntent().hasExtra("back")) {
            htab_header.setImageResource(R.drawable.back);
            this.muscle = "back";
        } else if (getIntent().hasExtra("cardio")) {
            htab_header.setImageResource(R.drawable.cardio);
            this.muscle = "cardio";
        } else if (getIntent().hasExtra("leg")) {
            htab_header.setImageResource(R.drawable.lowerleg);
            this.muscle = "lowerleg";
        } else if (getIntent().hasExtra("showall")) {
            htab_header.setImageResource(R.drawable.showall);
            this.muscle = "showall";
        }

    }

    public String getMyData() {
        return muscle;
    }

    private void setupViewPager() {
        viewPager = findViewById(R.id.htab_viewpager);
        //view pager and tab layout for swiping fragments
        viewPager.setAdapter(new SpecificMusclePagerAdapter(getSupportFragmentManager()));




    }

    public void handleClick(Exercise exercise) {

        Intent intent=new Intent(ExercisesActivity.this,SpecificExerciseActivity.class);
        //parcelable have size limit so i wont pass image bitmap with the exercise
        exercise.setPreviewBitmap(null);
        intent.putExtra("exercise",exercise);
        startActivity(intent);

    }
}
