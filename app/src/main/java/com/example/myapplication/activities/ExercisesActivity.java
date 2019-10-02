package com.example.myapplication.activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.myapplication.MyConstant;
import com.example.myapplication.R;
import com.example.myapplication.adapters.ExerciseAdapter;
import com.example.myapplication.model.Exercise;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExercisesActivity extends AppCompatActivity {
    private static final String TAG = "ExercisesActivity";
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);
        ButterKnife.bind(this);

        //set header image based on selected muscle from previous fragment
        setTabHeaderIamge();

        getExercise();
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


    public void handleClick(Exercise exercise) {

        Intent intent = new Intent(ExercisesActivity.this, SpecificExerciseActivity.class);
        //parcelable have size limit so i wont pass image bitmap with the exercise
        exercise.setPreviewBitmap(null);
        intent.putExtra("exercise", exercise);
        startActivity(intent);

    }


    //load exercises from firebase database
    private void getExercise() {
        final DatabaseReference exercisesNode = FirebaseDatabase.getInstance().getReference("excercises");
        DatabaseReference myRef = null;

        //get exercises only for the selected muscle by passing it from the exercise activity to this fragment
        switch (muscle) {
            case "triceps": {
                myRef = exercisesNode.child("triceps");
                break;
            }
            case "chest": {
                myRef = exercisesNode.child("chest");
                break;
            }
            case "shoulders": {
                myRef = exercisesNode.child("shoulders");
                break;
            }
            case "biceps": {
                myRef = exercisesNode.child("biceps");
                break;
            }
            case "abs": {
                myRef = exercisesNode.child("abs");
                break;
            }
            case "back": {
                myRef = exercisesNode.child("back");
                break;
            }
            case "cardio": {
                myRef = exercisesNode.child("cardio");
                break;
            }
            case "leg": {
                myRef = exercisesNode.child("leg");
                break;
            }
            case "showall": {
                myRef = exercisesNode.child("showall");
                break;
            }


        }
        //once we selected the right muscle group node this could will be the same for all exercises info
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            List<Exercise> exerciseList = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //only show in main list the exercises that admin added
                    if (ds.child("creatorId").getValue().equals(MyConstant.AdminId)) {
                        Exercise exercise = new Exercise();
                        exercise.setName(ds.child("name").getValue().toString());
                        exercise.setExecution(ds.child("execution").getValue().toString());
                        exercise.setPreparation(ds.child("preparation").getValue().toString());
                        exercise.setBodyPart(ds.child("bodyPart").getValue().toString());
                        exercise.setMechanism(ds.child("mechanism").getValue().toString());
                        exercise.setPreviewPhoto1(ds.child("previewPhoto1").getValue().toString());
                        exercise.setPreviewPhoto2(ds.child("previewPhoto2").getValue().toString());
                        exercise.setUtility(ds.child("utility").getValue().toString());
                        exercise.setVideoLink(ds.child("videoLink").getValue().toString());
                        exerciseList.add(exercise);
                    }
                }


                if (exerciseList.size() == 0) {
                    FancyToast.makeText(getApplicationContext(), "No exercises yet in this category", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    progressBar.setVisibility(View.GONE);
                }
                downloadExercisesImages(exerciseList, new CallbackInterface() {
                    @Override
                    public void callbackMethod(List<Exercise> exerciseList) {

                        //hide loading bar
                        progressBar.setVisibility(View.GONE);

                        setupRecycler(exerciseList);
                        setupSearchView();
                    }
                });
                //

                //after loading exercises show them in the recycler view

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void downloadExercisesImages(final List<Exercise> exerciseList, final CallbackInterface callbackInterface) {

        //download images and store them as bitmap in the model class so later we can show them in the adapter
        for (int i = 0; i < exerciseList.size(); i++) {
            //download preview image 1
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("exerciseImages/").child(exerciseList.get(i).getPreviewPhoto1());
            File localFile = null;
            try {
                localFile = File.createTempFile("images", "jpg");
                final int finalI = i;
                final File finalLocalFile = localFile;
                storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Local temp file has been created
                        exerciseList.get(finalI).setPreviewBitmap(BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath()));
                        callbackInterface.callbackMethod(exerciseList);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }


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

    private void setupRecycler(List<Exercise> exerciseList) {
        RecyclerView recyclerView = findViewById(R.id.exerciseRecyclerView);
        exerciseAdapter = new ExerciseAdapter(this, exerciseList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(exerciseAdapter);

    }


    private interface CallbackInterface {
        void callbackMethod(List<Exercise> exerciseList);
    }

}
