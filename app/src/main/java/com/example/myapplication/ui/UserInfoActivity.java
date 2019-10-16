package com.example.myapplication.ui;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestBuilder;
import com.example.myapplication.R;
import com.example.myapplication.adapters.ExerciseAdapter;
import com.example.myapplication.adapters.WorkoutAdapter;
import com.example.myapplication.model.Exercise;
import com.example.myapplication.model.User;
import com.example.myapplication.model.Workout;
import com.example.myapplication.utils.MyConstant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class UserInfoActivity extends AppCompatActivity {
    private static final String TAG = "UserInfoActivityy";


    @BindView(R.id.explainExerciseTextview)
    TextView explainExerciseTextview;

    @BindView(R.id.followersTextView)
    TextView followersTextView;


    @BindView(R.id.profile_image)
    ImageView profile_image;


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

    @BindView(R.id.followButton)
    Button followButton;

    @BindView(R.id.rateButton)
    Button rateButton;
    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.about_me_text)
    TextView aboutMeText;

    private UserInfoActivityViewModel mViewModel;

    @BindView(R.id.followingTextView)
    TextView followingTextView;

    @BindView(R.id.ratingTextView)
    TextView ratingTextView;


    private List<Exercise> exerciseListt = new ArrayList<>();

    private User user;
    private Boolean isSubscribed;
    private Observer<Boolean> observer;
    private ExerciseAdapter exerciseAdapter;
    private List<Workout> workoutListt = new ArrayList<>();
    private WorkoutAdapter workoutAdapter;
    private int rating;


    @OnClick(R.id.cardView)
    void showExerciseList() {
        setupExerciseRecycler();
    }

    @OnClick(R.id.cardView2)
    void showWorkoutList() {
        setupWorkoutRecycler();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);

        //note: user refers to the view profile user and not logged in user
        if (getIntent().hasExtra("user")) {
            user = getIntent().getParcelableExtra("user");

            showDataInView();

            //
            //

            mViewModel = ViewModelProviders.of(this).get(UserInfoActivityViewModel.class);
            //download user exercises
            mViewModel.getExercises(user.getId()).observe(this, new Observer<List<Exercise>>() {


                @Override
                public void onChanged(@Nullable List<Exercise> exerciseList) {
                    exerciseListt = exerciseList;
                    updateProfileExercisesView(exerciseList);
                }
            });
            //download user profile photo
            mViewModel.getUserPhoto(user.getPhoto()).observe(this, new Observer<RequestBuilder<Drawable>>() {
                @Override
                public void onChanged(@Nullable RequestBuilder<Drawable> drawableRequestBuilder) {
                    drawableRequestBuilder.into(profile_image);
                }
            });
            //get workouts list
            mViewModel.getWorkouts(user.getId()).observe(this, new Observer<List<Workout>>() {
                @Override
                public void onChanged(@Nullable List<Workout> workoutList) {
                    updateProfileWorkoutView(workoutList);
                    workoutListt = workoutList;

                }
            });
            //get follow state first time we open activity
            observer = new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean aBoolean) {
                    ChangeFollowButtonColors(aBoolean);
                    isSubscribed = aBoolean;

                }
            };
            mViewModel.getFollowState(user.getId()).observe(this, observer);
            //get followers count
            mViewModel.getFollowersCount(user.getId()).observe(this, new Observer<String>() {
                @Override
                public void onChanged(@Nullable String s) {
                    followersTextView.setText(s);
                }
            });
            //get rating average
            mViewModel.getRatingsAvg(user.getId()).observe(this, new Observer<Integer>() {
                @Override
                public void onChanged(@Nullable Integer integer) {

                    ratingTextView.setText(integer + "/5");
                }
            });
            //get following count
            mViewModel.getFollowingCount(user.getId()).observe(this, new Observer<String>() {
                @Override
                public void onChanged(@Nullable String s) {
                    followingTextView.setText(s);
                }
            });


            //get my rating and show it on rating bar
            mViewModel.getMyRate(user.getId()).observe(this, new Observer<Long>() {
                @Override
                public void onChanged(@Nullable Long aLong) {
                    //make rate button green since user rated him before
                    setMyRate(aLong);

                }
            });


        }
    }

    private void setMyRate(@Nullable Long aLong) {
        rateButton.setBackgroundColor(Color.parseColor("#49B03E"));
        rateButton.setTextColor(Color.parseColor("#E9EAEE"));
        rateButton.setText("my rate " + aLong + "/5");
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

    private void setupWorkoutRecycler() {

        if (workoutListt.size() == 0) {
            FancyToast.makeText(this, "This user didn't create any custom workouts yet.", FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();
        } else {
            RecyclerView recyclerView = findViewById(R.id.workoutRecycler);
            workoutAdapter = new WorkoutAdapter(this, "userInfo");
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            workoutAdapter.setDataSource(workoutListt);
            recyclerView.setAdapter(workoutAdapter);
        }

    }

    private void ChangeFollowButtonColors(Boolean aBoolean) {
        if (aBoolean == true) {
            followButton.setBackgroundColor(Color.parseColor("#49B03E"));
            followButton.setTextColor(Color.parseColor("#E9EAEE"));
            followButton.setText("followed");

        } else {
            followButton.setBackgroundColor(Color.parseColor("#49B03E"));
            followButton.setTextColor(Color.parseColor("#4CAF50b"));
            followButton.setText("follow");

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
        Log.i(TAG, "updateProfileWorkoutView: " + workoutList.size());
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
        email.setText(user.getEmail());
        if (user.getAbout_me() != null && !user.getAbout_me().equals("")) {
            aboutMeText.setText(user.getAbout_me());
        } else {
            aboutMeText.setText(user.getName() + " didn't add this information yet");
        }
        followingTextView.setText(user.getFollowing());

    }

    private void rate() {
        final View rate_view = getLayoutInflater().inflate(R.layout.rate_view, null);

        final RatingBar ratingBar = rate_view.findViewById(R.id.ratingBar);

   /*     ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rating = v;
            }
        });*/


        new AlertDialog.Builder(UserInfoActivity.this)
                .setTitle("Rate " + user.getName())
                .setView(rate_view)
                .setPositiveButton("Rate", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        rating = ratingBar.getProgress();
                        mViewModel.setRate(rating, user.getId()).observe(UserInfoActivity.this, new Observer<Boolean>() {
                            @Override
                            public void onChanged(Boolean aBoolean) {
                                if (aBoolean) setMyRate((long) rating);
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void follow() {
        {
            mViewModel.getFollowState(user.getId()).removeObserver(observer);

            if (!isSubscribed) {
                //add id of logged in user in followersUID in profile account
                final DatabaseReference profile = FirebaseDatabase.getInstance().getReference("users")
                        .child(user.getId()).child("followersUID");
                profile.push().setValue(MyConstant.loggedInUserId);
                //add id of profile account in followingUID of logged in account
                FirebaseDatabase.getInstance().getReference("users").child(MyConstant.loggedInUserId).child("followingUID")
                        .push().setValue(user.getId());
                //show toast
                FancyToast.makeText(UserInfoActivity.this, "Subscribed successfully", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                isSubscribed = true;
                ChangeFollowButtonColors(isSubscribed);
            } else {
                final DatabaseReference profile = FirebaseDatabase.getInstance().getReference("users")
                        .child(user.getId()).child("followersUID");
                profile.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren())
                            if (ds.getValue().equals(MyConstant.loggedInUserId)) {

                                String key = ds.getKey();
                                profile.child(key).removeValue();
                                //decrease followers count for this user by 1
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //
                final DatabaseReference followingUID = FirebaseDatabase.getInstance().
                        getReference("users").child(MyConstant.loggedInUserId).child("followingUID");
                followingUID.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.getValue().equals(user.getId())) {
                                String key = ds.getKey();
                                followingUID.child(key).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //
                //show toast
                FancyToast.makeText(UserInfoActivity.this, "Unsubscribed successfully.", FancyToast.LENGTH_SHORT, FancyToast.DEFAULT, false).show();

                isSubscribed = false;
                ChangeFollowButtonColors(isSubscribed);
            }

        }
    }


    @OnClick({R.id.followButton, R.id.rateButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.followButton:
                follow();
                break;
            case R.id.rateButton:
                rate();
                break;
        }
    }
}
