package com.example.myapplication.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.RequestBuilder;
import com.example.myapplication.R;
import com.example.myapplication.UserInfoActivityViewModel;
import com.example.myapplication.model.Exercise;
import com.example.myapplication.model.User;
import com.example.myapplication.model.Workout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;


public class UserInfoActivity extends AppCompatActivity {
    private static final String TAG = "UserInfoActivity";
    @BindView(R.id.explainExerciseTextview)
    TextView explainExerciseTextview;

    @BindView(R.id.followersTextView)
    TextView followersTextView;


    @BindView(R.id.profile_image)
    CircleImageView profile_image;


    @BindView(R.id.textViewName)
    TextView textViewName;

    @BindView(R.id.explainExerciseTextview2)
    TextView explainExerciseTextview2;
    @BindView(R.id.explainExerciseTextview3)
    TextView explainExerciseTextview3;
    @BindView(R.id.explainExerciseTextview4)
    TextView explainExerciseTextview4;

    @BindView(R.id.exercisesCountTextView)
    TextView exercisesCountTextView;

    @BindView(R.id.workoutCountTextView)
    TextView workoutCountTextView;
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

            //
            mViewModel = ViewModelProviders.of(this).get(UserInfoActivityViewModel.class);
            mViewModel.getExercises(user.getId()).observe(this, new Observer<List<Exercise>>() {
                @Override
                public void onChanged(@Nullable List<Exercise> exerciseList) {
                    updateProfileExercisesView(exerciseList);
                }
            });
            //
            mViewModel.getUserPhoto(user.getPhoto()).observe(this, new Observer<RequestBuilder<Drawable>>() {
                @Override
                public void onChanged(@Nullable RequestBuilder<Drawable> drawableRequestBuilder) {
                    Log.i(TAG, "onChanged: ");
                    drawableRequestBuilder.into(profile_image);
                }
            });
            //
            mViewModel.getWorkouts(user.getId()).observe(this, new Observer<List<Workout>>() {
                @Override
                public void onChanged(@Nullable List<Workout> workoutList) {
                    updateProfileWorkoutView(workoutList);
                }
            });

        }
    }

    private void updateProfileExercisesView(List<Exercise> exerciseList) {
        if (exerciseList.size() == 0) {
            explainExerciseTextview.setText(user.getName() + " has no custom exercises yet");
            explainExerciseTextview2.setVisibility(View.GONE);
        } else {
            explainExerciseTextview.setText(user.getName() + " created " + exerciseList.size() + " custom exercises");
        }

        exercisesCountTextView.setText(String.valueOf(exerciseList.size()));
    }

    private void updateProfileWorkoutView(List<Workout> workoutList) {
        if (workoutList.size() == 0) {
            explainExerciseTextview3.setText(user.getName() + " has no custom workouts yet");
            explainExerciseTextview4.setVisibility(View.GONE);
        } else {
            explainExerciseTextview.setText(user.getName() + " created " + workoutList.size() + " custom workouts");
        }

        workoutCountTextView.setText(String.valueOf(workoutList.size()));
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
