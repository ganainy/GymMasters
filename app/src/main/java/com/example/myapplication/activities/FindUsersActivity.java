package com.example.myapplication.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import com.example.myapplication.MyConstant;
import com.example.myapplication.R;
import com.example.myapplication.adapters.UserAdapter;
import com.example.myapplication.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FindUsersActivity extends AppCompatActivity {
    List<User> userList = new ArrayList<>();
    private UserAdapter userAdapter;

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
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("id").getValue().equals(MyConstant.loggedInUserId)) {
                        //don't show this user in list since it's the logged in user
                    } else {
                        User user = new User();
                        user.setName(ds.child("name").getValue().toString());
                        user.setPhoto(ds.child("photo").getValue().toString());
                        user.setFollowers(ds.child("followers").getValue().toString());
                        user.setRating(ds.child("rating").getValue().toString());
                        user.setFollowing(ds.child("following").getValue().toString());
                        user.setId(ds.child("id").getValue().toString());
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
        userAdapter = new UserAdapter(FindUsersActivity.this, userList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FindUsersActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(userAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //for filtering
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_users_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        //hide search button from keyboard since it does nothing and we filter on text change
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                userAdapter.getFilter().filter(s);
                return true;
            }
        });

        return true;
    }
}
