package com.example.myapplication.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class UserInfoActivity extends AppCompatActivity {
    private static final String TAG = "UserInfoActivity";

    @BindView(R.id.followersTextView)
    TextView followersTextView;

    @BindView(R.id.followingTextView)
    TextView followingTextView;

    @BindView(R.id.ratingTextView)
    TextView ratingTextView;
    private User user;

    @OnClick(R.id.followFab)
    void follow() {
        //todo continue follow code
        FirebaseDatabase.getInstance().getReference("users").child(user.getId())
                .child("followersUID").push().setValue(FirebaseAuth.getInstance().getUid());
        // String id = workoutRef.push().getKey();
        //workoutRef.child(id).setValue(this.workout);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);

        if (getIntent().hasExtra("user")) {
            user = (User) getIntent().getParcelableExtra("user");

            showDataInView();
        }
    }

    private void showDataInView() {
        followersTextView.setText(user.getFollowers());
        followingTextView.setText(user.getFollowing());
        if (user.getRating().equals("-1"))
            ratingTextView.setText("No rates yet");
        else
            ratingTextView.setText(user.getRating());
    }
}
