package com.example.myapplication.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.UserAdapter;
import com.example.myapplication.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FindUsersActivity extends AppCompatActivity {
    List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_users);

        loadAllUsers();
    }

    private void loadAllUsers() {
        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("id").getValue().equals(FirebaseAuth.getInstance().getUid())) {
                        //don't show this user in list since it's the logged in user
                    } else {
                        User user = new User();
                        user.setName(ds.child("name").getValue().toString());
                        user.setPhoto(ds.child("photo").getValue().toString());
                        user.setFollowers(ds.child("followers").getValue().toString());
                        user.setRating(ds.child("rating").getValue().toString());
                        userList.add(user);
                    }

                }
                setupRecycler();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void setupRecycler() {
        RecyclerView recyclerView = findViewById(R.id.usersRecycler);
        UserAdapter userAdapter = new UserAdapter(FindUsersActivity.this, userList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FindUsersActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(userAdapter);
    }
}
