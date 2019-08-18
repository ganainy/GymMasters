package com.example.myapplication.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.RequestBuilder;
import com.example.myapplication.R;
import com.example.myapplication.UserInfoActivityViewModel;
import com.example.myapplication.adapters.ExerciseAdapter;
import com.example.myapplication.model.Exercise;
import com.example.myapplication.model.User;
import com.example.myapplication.model.Workout;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;


public class UserInfoActivity extends AppCompatActivity {
    private static final String TAG = "UserInfoActivityy";
    private List<Exercise> exerciseListt;

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

    @BindView(R.id.followFab)
    FloatingActionButton followFab;

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
    private Boolean isSubscribed;
    private Observer<Boolean> observer;
    private ExerciseAdapter exerciseAdapter;

    @OnClick(R.id.followFab)
    void follow() {
        mViewModel.getFollowState(user.getId()).removeObserver(observer);
        //todo fix follow issue (can't follow then unfollow without leaving activity and coming back and vice-versa )
        mViewModel.followUnfollow(isSubscribed, user.getId()).observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Log.i(TAG, "fabClick: " + s);
                updateFab(s);
            }
        });

    }

    @OnClick(R.id.cardView)
    void showExerciseList() {

        setupExerciseRecycler();
    }

    private void updateFab(String s) {
        //change photo and color of fab depending on follow state
        if (s.equals("followdone")) {
            followFab.setImageResource(R.drawable.ic_following);
            followFab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#98FB98")));
        } else if (s.equals("unfollowdone")) {
            followFab.setImageResource(R.drawable.ic_follow);
            followFab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1E90FF")));

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);

        //note: user refers to the view profile user and not logged in user
        if (getIntent().hasExtra("user")) {
            user = (User) getIntent().getParcelableExtra("user");

            showDataInView();
            mViewModel = ViewModelProviders.of(this).get(UserInfoActivityViewModel.class);
            //
            mViewModel.getExercises(user.getId()).observe(this, new Observer<List<Exercise>>() {


                @Override
                public void onChanged(@Nullable List<Exercise> exerciseList) {
                    exerciseListt = exerciseList;
                    updateProfileExercisesView(exerciseList);
                }
            });
            //
            mViewModel.getUserPhoto(user.getPhoto()).observe(this, new Observer<RequestBuilder<Drawable>>() {
                @Override
                public void onChanged(@Nullable RequestBuilder<Drawable> drawableRequestBuilder) {
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
            //
            observer = new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean aBoolean) {
                    updateFab(aBoolean);
                    isSubscribed = aBoolean;
                    Log.i(TAG, "onChangedoncreate: " + aBoolean);
                }
            };
            mViewModel.getFollowState(user.getId()).observe(this, observer);

        }
    }

    private void setupExerciseRecycler() {
        if (exerciseListt.size() == 0) {
            FancyToast.makeText(this, "This user didn't create any custom exercises yet.", FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();
        } else {
            RecyclerView recyclerView = findViewById(R.id.exerciseRecycler);
            exerciseAdapter = new ExerciseAdapter(this, exerciseListt, "userInfo");
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(exerciseAdapter);
        }
    }

    private void updateFab(Boolean aBoolean) {
        //change photo and color of fab depending on follow state
        if (aBoolean == true) {
            followFab.setImageResource(R.drawable.ic_following);
            followFab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#98FB98")));
        } else {
            followFab.setImageResource(R.drawable.ic_follow);
            followFab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1E90FF")));

        }

    }

    private void updateProfileExercisesView(List<Exercise> exerciseList) {
        if (exerciseList.size() == 0) {
            explainExerciseTextview.setText(user.getName() + " has no custom exercises yet");
            explainExerciseTextview2.setVisibility(View.GONE);
        } else {
            explainExerciseTextview2.setVisibility(View.VISIBLE);
            explainExerciseTextview.setText(user.getName() + " created " + exerciseList.size() + " custom exercises");
        }

        exercisesCountTextView.setText(String.valueOf(exerciseList.size()));
    }
    private void updateProfileWorkoutView(List<Workout> workoutList) {
        if (workoutList.size() == 0) {
            explainExerciseTextview3.setText(user.getName() + " has no custom workouts yet");
            explainExerciseTextview4.setVisibility(View.GONE);
        } else {
            explainExerciseTextview4.setVisibility(View.VISIBLE);
            explainExerciseTextview3.setText(user.getName() + " created " + workoutList.size() + " custom workouts");
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
            ratingTextView.setText(user.getRating() + "/5");
    }
}
