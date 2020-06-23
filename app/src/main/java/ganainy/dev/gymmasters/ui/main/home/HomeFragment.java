package ganainy.dev.gymmasters.ui.main.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;

import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.ui.main.ActivityCallback;
import ganainy.dev.gymmasters.utils.AuthUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HomeFragment extends Fragment {
    private static final String TAG = "MainFragmentHome";
    public static final String USER_ID = "userId";

    @BindView(R.id.viewLoggedUserExercises)
    Button viewMyExercisesButton;
    @BindView(R.id.viewMyWorkoutsButton)
    Button viewMyWorkoutsButton;
    @BindView(R.id.textViewName)
    TextView name;
    @BindView(R.id.emailTextInputLayout)
    TextView email;
    @BindView(R.id.aboutUserContentTextView)
    TextView aboutMeText;
    @BindView(R.id.profile_image)
    ImageView profileImage;
    @BindView(R.id.followersCountTextView)
    TextView followersTextView;
    @BindView(R.id.followingCountTextView)
    TextView followingTextView;
    @BindView(R.id.ratingAverageTextView)
    TextView ratingTextView;
    @BindView(R.id.shimmer_layout)
    ShimmerFrameLayout shimmerFrameLayout;

    private HomeViewModel mViewModel;


    @OnClick(R.id.createWorkout)
    void createWorkout() {
        ActivityCallback activityCallback = (ActivityCallback) requireActivity();
        activityCallback.openCreateWorkoutFragment();
    }

    @OnClick(R.id.createExercise)
    void createExercise() {
        ActivityCallback activityCallback = (ActivityCallback) requireActivity();
        activityCallback.openCreateExerciseFragment();
    }

    @OnClick(R.id.viewLoggedUserExercises)
    void viewLoggedUserExercises() {
        ActivityCallback activityCallback = (ActivityCallback) requireActivity();
        activityCallback.openUserExercisesFragment(AuthUtils.getLoggedUserId(requireContext()),null);
    }

    @OnClick(R.id.viewMyWorkoutsButton)
    void viewWorkouts() {
        ActivityCallback activityCallback = (ActivityCallback) requireActivity();
        activityCallback.openUserWorkoutsFragment(AuthUtils.getLoggedUserId(requireContext()),null);
    }


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        mViewModel.getUserData(AuthUtils.getLoggedUserId(requireContext()));
        mViewModel.getFollowersCount(AuthUtils.getLoggedUserId(requireContext()));
        mViewModel.getFollowingCount(AuthUtils.getLoggedUserId(requireContext()));
        mViewModel.getRatingsAvg(AuthUtils.getLoggedUserId(requireContext()));

        mViewModel.getUserLiveData().observe(getViewLifecycleOwner(), this::setupUi);

        mViewModel.getFollowersCountLiveData().observe(getViewLifecycleOwner(),followerCount->{
            followersTextView.setText(followerCount);
        });

        mViewModel.getFollowingCountLiveData().observe(getViewLifecycleOwner(),followingCount->{
            followingTextView.setText(followingCount);
        });


        mViewModel.getRatingAverageLiveData().observe(getViewLifecycleOwner(),averageRating->{
            ratingTextView.setText(getString(R.string.rating_formula,averageRating));
        });

        mViewModel.getUpdateAboutMe().observe(getViewLifecycleOwner(),aboutMePair->{
            if (aboutMePair.first){
                //update was successful
                aboutMeText.setText(aboutMePair.second);
            }else {
                //update failed, keep old about me
            }
        });
    }

    private void setupUi(User user) {
        name.setText(user.getName());
        email.setText(user.getEmail());
        aboutMeText.setText(user.getAbout_me());
        Glide.with(HomeFragment.this).load(user.getPhoto()).into(profileImage);
        shimmerFrameLayout.hideShimmer();
    }



    @OnClick(R.id.edit)
    public void onEditAboutMeClicked() {
        showAlertDialog();
    }

    private void showAlertDialog() {
        final View alertDialogView = getLayoutInflater().inflate(R.layout.edit_view, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(alertDialogView)
                .show();

        Button saveAlertDialog = alertDialogView.findViewById(R.id.save_action);
        Button cancelAlertDialog = alertDialogView.findViewById(R.id.cancel_action);

        saveAlertDialog.setOnClickListener(view -> {
            EditText editText = alertDialogView.findViewById(R.id.editText);
            mViewModel.updateAboutMe(editText.getText().toString(),AuthUtils.getLoggedUserId(requireContext()));
            alertDialog.dismiss();
        });
        cancelAlertDialog.setOnClickListener(view -> alertDialog.dismiss());
    }
}
