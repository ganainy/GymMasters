package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.SpecificMusclePagerAdapter;
import com.example.myapplication.model.Exercise;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExercisesActivity extends AppCompatActivity {
    ViewPager viewPager;
    String muscle;

    @BindView(R.id.htab_collapse_toolbar)
    CollapsingToolbarLayout htab_collapse_toolbar;

    @OnClick(R.id.addExerciseFab)
    void openAddNewExerciseActivity() {

        startActivity(new Intent(ExercisesActivity.this, AddNewExerciseActivity.class));


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);
        ButterKnife.bind(this);


        if (getIntent().hasExtra("triceps")) {
            ImageView imageView = findViewById(R.id.htab_header);
            imageView.setImageResource(R.drawable.triceps);
            this.muscle = "triceps";
        }
        //TODO add elseif for other muscles

        setupViewPager();
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
