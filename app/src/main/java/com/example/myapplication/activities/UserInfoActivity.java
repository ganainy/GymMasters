package com.example.myapplication.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.UserInfoActivityViewModel;
import com.example.myapplication.model.Exercise;
import com.example.myapplication.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class UserInfoActivity extends AppCompatActivity {
    private static final String TAG = "UserInfoActivity";
    @BindView(R.id.explainExerciseTextview)
    TextView explainExerciseTextview;

    @BindView(R.id.followersTextView)
    TextView followersTextView;
    @BindView(R.id.textViewName)
    TextView textViewName;
    @BindView(R.id.explainExerciseTextview2)
    TextView explainExerciseTextview2;
    @BindView(R.id.exercisesCountTextView)
    TextView exercisesCountTextView;
    private UserInfoActivityViewModel mViewModel;

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

            mViewModel = ViewModelProviders.of(this).get(UserInfoActivityViewModel.class);
            mViewModel.init(user.getId());
            mViewModel.getExercises().observe(this, new Observer<List<Exercise>>() {
                @Override
                public void onChanged(@Nullable List<Exercise> exerciseList) {
                    updateProfileExercisesView(exerciseList);
                }
            });
        }
    }

    private void updateProfileExercisesView(List<Exercise> exerciseList) {
        if (exerciseList.size() == 0) {
            explainExerciseTextview.setText(user.getName() + " has no custom workouts yet");
            explainExerciseTextview2.setVisibility(View.GONE);
        } else {
            explainExerciseTextview.setText(user.getName() + " created " + exerciseList.size() + " custom workouts");
        }

        exercisesCountTextView.setText(String.valueOf(exerciseList.size()));
    }

    private void showDataInView() {
        textViewName.setText(user.getName());
        followersTextView.setText(user.getFollowers());
        followingTextView.setText(user.getFollowing());
        if (user.getRating().equals("-1"))
            ratingTextView.setText("No rates yet");
        else
            ratingTextView.setText(user.getRating());
    }
}
