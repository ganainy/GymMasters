package com.example.myapplication;

import android.app.Application;
import android.support.annotation.NonNull;

import com.example.myapplication.activities.MainActivity;
import com.example.myapplication.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivityRepository {


    public MainActivityRepository(Application application) {

    }
        FirebaseAuth mAuth;
        private String profilePictureId;
        private String name,email,rating;

        public void getUserData(final MainActivity.FirebaseCallback firebaseCallback) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("users");

            mAuth = FirebaseAuth.getInstance();
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    profilePictureId=dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("photo").getValue().toString();
                    name=dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("name").getValue().toString();
                    email=dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("email").getValue().toString();
                    rating=dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("rating").getValue().toString();


                    firebaseCallback.onCallback(new User(name,email,rating,profilePictureId));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


}

