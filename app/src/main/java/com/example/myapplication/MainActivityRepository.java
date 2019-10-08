package com.example.myapplication;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivityRepository {
    private static final String TAG = "MainActivityRepository";
    User user;
    MutableLiveData<User> userMutableLiveData;
    public MainActivityRepository(Application application) {


    }
        FirebaseAuth mAuth;
        private String profilePictureId;
    private String name, email, rating, id;

    public LiveData<User> getUserData() {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("users");
        userMutableLiveData = new MutableLiveData<>();

            mAuth = FirebaseAuth.getInstance();
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = new User();

                    profilePictureId=dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("photo").getValue().toString();
                    name=dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("name").getValue().toString();
                    email=dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("email").getValue().toString();
                    id = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("id").getValue().toString();

                    user.setEmail(email);
                    user.setPhoto(profilePictureId);
                    user.setName(name);
                    user.setId(id);

                    userMutableLiveData.setValue(user);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        return userMutableLiveData;
        }


    private void loadUserPhoto(User loggedInUser) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        //reference to logged in user profile image

        StorageReference pathReference = storageRef.child("images/" + loggedInUser.getPhoto());
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //download image with glide then show it in the navigation menu
                Log.i(TAG, "onSuccess: loaded from storage");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.i(TAG, "onFailure: " + exception.getMessage());
            }
        });
    }


    public LiveData<Uri> getUserPhoto(User user) {
        final MutableLiveData<Uri> load = new MutableLiveData<>();
        FirebaseStorage.getInstance().getReference().child("images/").child(user.getPhoto()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                load.setValue(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: " + e.getMessage());
            }
        });
        return load;
    }

}

