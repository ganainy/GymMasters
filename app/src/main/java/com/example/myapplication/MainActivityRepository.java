package com.example.myapplication;

import android.app.Application;

import androidx.annotation.NonNull;

import com.example.myapplication.model.User;
import com.example.myapplication.ui.MainActivity;
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
    private String name, email, rating, id;

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
                    id = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("id").getValue().toString();


                    firebaseCallback.onCallback(new User(id, name, email, profilePictureId));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }



}

