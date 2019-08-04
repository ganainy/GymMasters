package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;

import com.example.myapplication.R;
import com.example.myapplication.adapters.SpecificMusclePagerAdapter;
import com.example.myapplication.adapters.ViewPagerAdapterMainActivity;
import com.example.myapplication.fragments.SpecificMuscleFragment;
import com.example.myapplication.model.Exercise;

public class ExercisesActivity extends AppCompatActivity {
    ViewPager viewPager;
    String muscle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


        if (getIntent().hasExtra("triceps")) {
            ImageView imageView = findViewById(R.id.htab_header);
            imageView.setImageResource(R.drawable.triceps);
            this.muscle = "triceps";
        }
        //TODO add elseif for other muscles

        setupViewPagerAndTabLayout();
    }

    public String getMyData() {
        return muscle;
    }

    private void setupViewPagerAndTabLayout() {
        viewPager = findViewById(R.id.htab_viewpager);
        //view pager and tab layout for swiping fragments
        viewPager.setAdapter(new SpecificMusclePagerAdapter(getSupportFragmentManager()));
        TabLayout tabLayout = findViewById(R.id.htab_tabs);
        tabLayout.setupWithViewPager(viewPager, true);

//        tabLayout.getTabAt(0).setIcon(R.drawable.ic_crisscross_position);
//        tabLayout.getTabAt(1).setIcon(R.drawable.ic_dumbbell_variant_outline);

        tabLayout.getTabAt(0).setText(("Exercises"));
        tabLayout.getTabAt(1).setText(("Favourite Exercises"));


    }

    public void handleClick(Exercise exercise) {

        Intent intent=new Intent(ExercisesActivity.this,SpecificExerciseActivity.class);
        intent.putExtra("exercise",exercise);
        startActivity(intent);

    }
}
