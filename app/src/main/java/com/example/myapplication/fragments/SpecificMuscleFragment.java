package com.example.myapplication.fragments;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.myapplication.R;
import com.example.myapplication.activities.ExercisesActivity;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class SpecificMuscleFragment extends Fragment {


    ExerciseAdapter exerciseAdapter;
    private static final String TAG = "SpecificMuscleFragment";
    private ProgressBar progressBar;

    public SpecificMuscleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_specific_muscle, container, false);

    //get selected muscle from  ExercisesActivity
        ExercisesActivity activity = (ExercisesActivity) getActivity();
        String myDataFromActivity = activity.getMyData();
        Log.i(TAG, "onCreateView: " + myDataFromActivity);




        getExercise(myDataFromActivity, view);


        return view;
    }


    //load exercises from firebase database
    private void getExercise(String myDataFromActivity, final View view) {
        final DatabaseReference exercisesNode = FirebaseDatabase.getInstance().getReference("excercises");
        DatabaseReference myRef = null;

        //get exercises only for the selected muscle by passing it from the exercise activity to this fragment
        switch (myDataFromActivity) {
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
            case "forearm": {
                myRef = exercisesNode.child("forearm");
                break;
            }
            case "upperleg": {
                myRef = exercisesNode.child("upperleg");
                break;
            }
            case "glutes": {
                myRef = exercisesNode.child("glutes");
                break;
            }
            case "cardio": {
                myRef = exercisesNode.child("cardio");
                break;
            }
            case "lowerleg": {
                myRef = exercisesNode.child("lowerleg");
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


                progressBar = view.findViewById(R.id.progressBar);
                if (exerciseList.size() == 0) {
                    FancyToast.makeText(getActivity(), "No exercises yet in this category", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    progressBar.setVisibility(View.GONE);
                }
                downloadExercisesImages(exerciseList, new CallbackInterface() {
                    @Override
                    public void callbackMethod(List<Exercise> exerciseList) {

                        //hide loading bar
                        progressBar.setVisibility(View.GONE);

                        setupRecycler(view, exerciseList);
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
        ExercisesActivity exercisesActivity=(ExercisesActivity)getActivity();
        SearchView searchView=exercisesActivity.findViewById(R.id.search_view);
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
    private void setupRecycler(View view, List<Exercise> exerciseList) {
        RecyclerView recyclerView = view.findViewById(R.id.exerciseRecyclerView);
         exerciseAdapter = new ExerciseAdapter(getActivity(),exerciseList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(exerciseAdapter);
     
    }






    private interface CallbackInterface
    {
         void callbackMethod(List<Exercise> exerciseList);
    }




}
