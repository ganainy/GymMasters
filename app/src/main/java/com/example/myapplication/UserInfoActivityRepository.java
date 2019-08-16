package com.example.myapplication;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.myapplication.model.Exercise;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class UserInfoActivityRepository {
    private static final String TAG = "UserInfoActivityReposit";
    private static UserInfoActivityRepository instance;
    private List<Exercise> exerciseList = new ArrayList<>();

    public static UserInfoActivityRepository getInstance() {
        if (instance == null) {
            instance = new UserInfoActivityRepository();
        }
        return instance;
    }

    public MutableLiveData<List<Exercise>> getExercises(final String profileId) {
        final MutableLiveData<List<Exercise>> data = new MutableLiveData<>();
        exerciseList.clear();
        final Exercise exercise = new Exercise();
        DatabaseReference exerciseNode = FirebaseDatabase.getInstance().getReference("excercises");
        exerciseNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsBig : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds : dsBig.getChildren()) {
                        Log.i(TAG, "onDataChange: " + profileId);
                        if (ds.child("creatorId").getValue().equals(profileId)) {
                            Log.i(TAG, "onDataChange: true");
                            exercise.setExecution(ds.child("execution").getValue().toString());
                            exercise.setPreparation(ds.child("preparation").getValue().toString());
                            exercise.setMechanism(ds.child("mechanism").getValue().toString());
                            exercise.setPreviewPhoto1(ds.child("previewPhoto1").getValue().toString());
                            exercise.setPreviewPhoto2(ds.child("previewPhoto2").getValue().toString());
                            exercise.setUtility(ds.child("utility").getValue().toString());
                            exercise.setVideoLink(ds.child("videoLink").getValue().toString());
                            exerciseList.add(exercise);
                        }
                    }
                }
                data.setValue(exerciseList);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "onCancelled: " + databaseError.getMessage() + " %%% " + databaseError.getDetails());
            }
        });
        return data;
    }


    public MutableLiveData<RequestBuilder<Drawable>> downloadUserPhoto(final Application application, String photo) {
        final MutableLiveData<RequestBuilder<Drawable>> load = new MutableLiveData<>();
        FirebaseStorage.getInstance().getReference().child("images/").child(photo).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i(TAG, "onSuccesss: ");
                RequestBuilder<Drawable> load1 = Glide.with(application.getApplicationContext()).load(uri.toString());
                load.setValue(load1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: ");
            }
        });

        return load;
    }
}


