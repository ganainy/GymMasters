package ganainy.dev.gymmasters.ui.userInfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.models.app_models.Workout;
import ganainy.dev.gymmasters.ui.main.ActivityCallback;

public class UserInfoFragment extends Fragment {

    public static final String USER = "user";
    private UserInfoViewModel mViewModel;

    private static final String TAG = "UserInfoFragmentTag";

    @BindView(R.id.exerciseCountFullTextView)
    TextView exerciseCountFullTextView;

    @BindView(R.id.followersCountTextView)
    TextView followersTextView;

    @BindView(R.id.profile_image)
    ImageView profile_image;

    @BindView(R.id.textViewName)
    TextView textViewName;

    @BindView(R.id.workoutCountFullTextView)
    TextView workoutCountFullTextView;

    @BindView(R.id.exercisesCountTextView)
    TextView exercisesCountTextView;

    @BindView(R.id.workoutCountTextView)
    TextView workoutCountTextView;

    @BindView(R.id.clickToViewExercises)
    TextView clickToViewExercises;

    @BindView(R.id.clickToViewWorkouts)
    TextView clickToViewWorkouts;

    @BindView(R.id.followButton)
    Button followButton;

    @BindView(R.id.rateButton)
    ImageButton rateButton;

    @BindView(R.id.emailTextInputLayout)
    TextView email;

    @BindView(R.id.aboutUserContentTextView)
    TextView aboutUserContentTextView;

    @BindView(R.id.aboutUserTitleTextView)
    TextView aboutUserTitleTextView;

    @BindView(R.id.followingCountTextView)
    TextView followingTextView;

    @BindView(R.id.ratingAverageTextView)
    TextView ratingTextView;

    @BindView(R.id.showWorkoutsLayout)
    ConstraintLayout showWorkoutsLayout;

    @BindView(R.id.showExercisesLayout)
    ConstraintLayout showExercisesLayout;

    @OnClick(R.id.followButton)
    void onFollowButtonClick() {
        mViewModel.followUnfollow();
    }

    @OnClick(R.id.rateButton)
    void onRateButtonClick() {
        rate();
    }

   /* @OnClick(R.id.backArrowImageView)
    public void onViewClicked() {
        requireActivity().onBackPressed();
    }*/

    private int rating;

    @OnClick(R.id.showExercisesLayout)
    void showExerciseList() {
        ActivityCallback activityCallback = (ActivityCallback) requireActivity();
        activityCallback.openUserExercisesFragment(mViewModel.getUserProfileModel().getProfileOwner().getId(),
                mViewModel.getUserProfileModel().getProfileOwner().getName());
    }

    @OnClick(R.id.showWorkoutsLayout)
    void showWorkoutList() {
        ActivityCallback activityCallback = (ActivityCallback) requireActivity();
        activityCallback.openUserWorkoutsFragment(mViewModel.getUserProfileModel().getProfileOwner().getId(),
                mViewModel.getUserProfileModel().getProfileOwner().getName());
    }


    /**
     * @param user : the user which we will show the profile of (not logged in user)
     */
    public static UserInfoFragment newInstance(User user) {
        UserInfoFragment userInfoFragment = new UserInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(USER, user);
        userInfoFragment.setArguments(bundle);
        return userInfoFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_info_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if (getArguments().getParcelable(USER) != null) {
            User mUser = getArguments().getParcelable(USER);
            initViewModel(mUser);

            /*any changes in ui will be reflected using this observer*/
            mViewModel.getUserProfileModelLiveData().observe(getViewLifecycleOwner(),userProfileModel -> {
                showDataInView(userProfileModel.getProfileOwner());
                updateProfileExercisesView(userProfileModel.getExercisesList(),userProfileModel.getProfileOwner().getName());
                updateProfileWorkoutView(userProfileModel.getWorkoutList(),userProfileModel.getProfileOwner().getName());
                updateFollowState(userProfileModel.getFollowState());
                if (userProfileModel.getFollowersCount()!=null)
                followersTextView.setText(userProfileModel.getFollowersCount().toString());
                if (userProfileModel.getRatingAverage()!=null)
                ratingTextView.setText(userProfileModel.getRatingAverage() + "/5");
                if (userProfileModel.getFollowingCount()!=null)
                followingTextView.setText(userProfileModel.getFollowingCount().toString());
                if (userProfileModel.getLoggedUserRating()!=null)
                setMyRate(userProfileModel.getLoggedUserRating());
                if (userProfileModel.getProfileOwner().getName()!=null)
                setAboutCardTitle(userProfileModel.getProfileOwner().getName());

            });

            //download user profile photo
            Glide.with(requireActivity()).load(mUser.getPhoto())
                    .apply(new RequestOptions().placeholder(R.drawable.loading_animation).error(R.drawable.anonymous_profile))
                    .into(profile_image);
        }

    }

    private void setAboutCardTitle(String profileOwnerName) {
        aboutUserTitleTextView.setText("About "+profileOwnerName+" :");
    }

    private void updateFollowState(FollowState followState) {
        switch (followState) {
            case FOLLOWING:
            case NOT_FOLLOWING:
                ChangeFollowButtonColors(followState);
                break;
            case ERROR:
                break;
        }
    }

    private void initViewModel(User mUser) {
        UserInfoViewModelFactory userInfoViewModelFactory = new UserInfoViewModelFactory(requireActivity().getApplication(),mUser);
        mViewModel = new ViewModelProvider(this, userInfoViewModelFactory).get(UserInfoViewModel.class);
    }


    private void setMyRate(@Nullable Long aLong) {
        rateButton.setBackground(getResources().getDrawable(R.drawable.circular_green_bordersolid));//green
        rateButton.setImageResource(R.drawable.ic_star_yellow);
     //   rateButton.setText("i rated: " + aLong + "/5");
    }

    private void ChangeFollowButtonColors(FollowState followState) {

        if (followState == FollowState.FOLLOWING) {
            followButton.setBackgroundResource((R.drawable.btn_add_green)); //green
            followButton.setText("following");
        } else if (followState == FollowState.NOT_FOLLOWING) {
            followButton.setBackgroundResource((R.drawable.btn_add_blue));
            followButton.setText("follow");
        }
    }

    private void updateProfileExercisesView(List<Exercise> exerciseList,String profileOwnerName) {
        if (exerciseList==null || exerciseList.isEmpty()) {
            exerciseCountFullTextView.setText(profileOwnerName + " has no custom exercises yet");
            clickToViewExercises.setVisibility(View.GONE);
            exercisesCountTextView.setText("0");
        } else {
            clickToViewExercises.setVisibility(View.VISIBLE);
            exerciseCountFullTextView.setText(profileOwnerName + " created " + exerciseList.size() + " custom exercises");
            exercisesCountTextView.setText(Integer.toString(exerciseList.size()));
        }
    }

    private void updateProfileWorkoutView(List<Workout> workoutList,String profileOwnerName) {
        if (workoutList==null || workoutList.isEmpty()) {
            workoutCountFullTextView.setText(profileOwnerName + " has no custom workouts yet");
            clickToViewWorkouts.setVisibility(View.GONE);
            workoutCountTextView.setText("0");
        } else {
            clickToViewWorkouts.setVisibility(View.VISIBLE);
            workoutCountFullTextView.setText(profileOwnerName + " created " + workoutList.size() + " custom workouts");
            workoutCountTextView.setText(Integer.toString(workoutList.size()));
        }

    }

    private void showDataInView(User profileOwner) {
        textViewName.setText(profileOwner.getName());
        email.setText(profileOwner.getEmail());
        if (profileOwner.getAbout_me() != null && !profileOwner.getAbout_me().equals("")) {
            aboutUserContentTextView.setText(profileOwner.getAbout_me());
        } else {
            aboutUserContentTextView.setText(profileOwner.getName() + " didn't add this information yet");
        }
    }

    private void rate() {

        final View rate_view = getLayoutInflater().inflate(R.layout.rate_view, null);
        final RatingBar ratingBar = rate_view.findViewById(R.id.ratingBar);

        new AlertDialog.Builder(requireActivity())
                .setTitle("Rate user")
                .setView(rate_view)
                .setPositiveButton("Rate", (dialog, which) -> {

                    rating = ratingBar.getProgress();
                    mViewModel.setRate((long) rating);

                })
                .setNegativeButton("Cancel", null)
                .show();
    }


}