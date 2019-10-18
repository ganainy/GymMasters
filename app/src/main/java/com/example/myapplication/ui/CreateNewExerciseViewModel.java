package com.example.myapplication.ui;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.model.Exercise;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CreateNewExerciseViewModel extends ViewModel {
    private static final String TAG = "CreateNewExerciseViewMo";

    public MutableLiveData<Boolean> checkRepeation(final String name) {
        final MutableLiveData<Boolean> load = new MutableLiveData<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("excercises").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsBig : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds : dsBig.getChildren()) {
                        if (ds.child("name").getValue().toString().toLowerCase().trim().equals(name.toLowerCase().trim())) {
                            load.setValue(true);
                            return;
                        }
                    }
                }
                load.setValue(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "onCancelled: ");
            }
        });

        return load;
    }


    public MutableLiveData<Boolean> uploadExercise(final Exercise exercise) {


        final MutableLiveData<Boolean> isSuccessful = new MutableLiveData<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


        Task<Void> excercises = reference.child("excercises").child(exercise.getBodyPart().toLowerCase()).push().setValue(exercise);
        excercises.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                isSuccessful.setValue(true);

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isSuccessful.setValue(false);
                Log.i(TAG, "onFailure: " + e.getMessage());
            }
        });

        return isSuccessful;

    }


    public MutableLiveData<Boolean> uploadExercisePhotos(Uri imageUri, final Uri image2Uri, final long timeMilli) {
        final MutableLiveData<Boolean> load = new MutableLiveData<>();
        // Create a storage reference from our app
        final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference imagesRef = storageRef.child("exerciseImages/" + imageUri.getLastPathSegment() + timeMilli);
        imagesRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                try {
                    final StorageReference imagesRef2 = storageRef.child("exerciseImages/" + image2Uri.getLastPathSegment() + timeMilli);
                    imagesRef2.putFile(image2Uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            load.setValue(true);
                        }
                    });
                } catch (Exception e) {
                    Log.i(TAG, " failed uploading second photo" + e.getMessage());
                    load.setValue(false);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: " + e.getMessage());
                load.setValue(false);
            }
        });

        return load;
    }
}
