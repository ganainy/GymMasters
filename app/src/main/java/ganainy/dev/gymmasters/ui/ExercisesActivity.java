package ganainy.dev.gymmasters.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.adapters.ExerciseAdapter;
import ganainy.dev.gymmasters.model.Exercise;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExercisesActivity extends AppCompatActivity {
    private static final String TAG = "ExercisesActivity";
    ExerciseActivityViewModel exerciseActivityViewModel;
    String muscle;

    @BindView(R.id.collapse_toolbar)
    CollapsingToolbarLayout htab_collapse_toolbar;

    @BindView(R.id.htab_header)
    ImageView htab_header;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.search_view)
    SearchView searchView;
    private ExerciseAdapter exerciseAdapter;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);
        ButterKnife.bind(this);


        recyclerView = findViewById(R.id.exerciseRecyclerView);


        /**set header image based on selected muscle from previous fragment*/
        setTabHeaderIamge();


        exerciseActivityViewModel = ViewModelProviders.of(this).get(ExerciseActivityViewModel.class);

        exerciseActivityViewModel.getExercises(muscle).observe(this, new Observer<List<Exercise>>() {
            @Override
            public void onChanged(@Nullable List<Exercise> exercises) {
                /**setting up recycler here without images since image loading might take a while*/
                setupRecycler(exercises);
                exerciseActivityViewModel.downloadExercisesImages(exercises).observe(ExercisesActivity.this, new Observer<List<Exercise>>() {
                    @Override
                    public void onChanged(@Nullable List<Exercise> exercises) {
                        /** setting up recycler again with image*/
                        setupRecycler(exercises);
                    }
                });
            }
        });

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
            this.muscle = "leg";
        } else if (getIntent().hasExtra("showall")) {
            htab_header.setImageResource(R.drawable.showall);
            this.muscle = "showall";
        }

    }


    public void handleClick(Exercise exercise) {

        Intent intent = new Intent(ExercisesActivity.this, SpecificExerciseActivity.class);
        /**parcelable have size limit so i won't pass image bitmap with the exercise*/
        exercise.setPreviewBitmap(null);
        intent.putExtra("exercise", exercise);
        startActivity(intent);

    }



    private void setupRecycler(List<Exercise> exerciseList) {

        progressBar.setVisibility(View.GONE);

        if (exerciseList.size() == 0) {
            FancyToast.makeText(getApplicationContext(), "No exercises yet in this category", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
        } else {
            exerciseAdapter = new ExerciseAdapter(this, exerciseList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(exerciseAdapter);
            setupSearchView();
        }

    }

    private void setupSearchView() {
        //do filtering when i type in search or click search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryString) {

                exerciseAdapter.getFilter().filter(queryString);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String queryString) {

                exerciseAdapter.getFilter().filter(queryString);

                return false;
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        recyclerView.setAdapter(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        if (exerciseAdapter != null) recyclerView.setAdapter(exerciseAdapter);
    }
}
