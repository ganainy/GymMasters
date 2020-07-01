package ganainy.dev.gymmasters.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.facebook.shimmer.ShimmerFrameLayout;

import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.ui.main.ActivityCallback;
import ganainy.dev.gymmasters.utils.AuthUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static ganainy.dev.gymmasters.ui.main.MainActivity.FOLLOWERS;
import static ganainy.dev.gymmasters.ui.main.MainActivity.FOLLOWING;
import static ganainy.dev.gymmasters.ui.main.MainActivity.SOURCE;


public class ProfileFragment extends Fragment {
    private static final String TAG = "MainFragmentHome";
    public static final String USER_ID = "userId";
    private static final int PICK_IMAGE = 101;

    private Handler mHandler =new Handler();

    @BindView(R.id.viewLoggedUserExercises)
    Button viewMyExercisesButton;
    @BindView(R.id.viewUsersIamFollowingButton)
    Button viewMyWorkoutsButton;
    @BindView(R.id.textViewName)
    TextView name;
    @BindView(R.id.emailTextInputLayout)
    TextView email;
    @BindView(R.id.aboutUserContentTextView)
    TextView aboutUserContentTextView;
    @BindView(R.id.profile_image)
    ImageView profileImage;
    @BindView(R.id.followersCountTextView)
    TextView followersTextView;
    @BindView(R.id.followingCountTextView)
    TextView followingTextView;
    @BindView(R.id.ratingAverageTextView)
    TextView ratingTextView;

    @BindView(R.id.nameShimmer)
    ShimmerFrameLayout nameShimmer;

    @BindView(R.id.emailShimmer)
    ShimmerFrameLayout emailShimmer;

    @BindView(R.id.followerCountShimmer)
    ShimmerFrameLayout followerCountShimmer;

    @BindView(R.id.followingCountShimmer)
    ShimmerFrameLayout followingCountShimmer;

    @BindView(R.id.ratingAverageShimmer)
    ShimmerFrameLayout ratingAverageShimmer;

    @BindView(R.id.aboutUserContentShimmer)
    ShimmerFrameLayout aboutUserContentShimmer;

    @BindView(R.id.progressBar_upload_image)
    ProgressBar uploadImageProgressBar;


    private ProfileViewModel mViewModel;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }


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

    @OnClick(R.id.viewLoggedUserWorkouts)
    void viewWorkouts() {
        ActivityCallback activityCallback = (ActivityCallback) requireActivity();
        activityCallback.openUserWorkoutsFragment(AuthUtils.getLoggedUserId(requireContext()),null);
    }

    @OnClick(R.id.viewMyFollowersButton)
    void onViewMyFollowersClick() {
        ActivityCallback activityCallback = (ActivityCallback) requireActivity();
        activityCallback.showLoggedUserFollowers(SOURCE, FOLLOWERS);
    }

    @OnClick(R.id.viewUsersIamFollowingButton)
    void onViewUsersIamFollowingClick() {
        ActivityCallback activityCallback = (ActivityCallback) requireActivity();
        activityCallback.showUsersFollowedByLoggedUser(SOURCE, FOLLOWING);
    }

    @OnClick(R.id.image_button_change_picture)
    void onChangeProfileImageClick() {
        openImageChooser();
    }

    @OnClick(R.id.edit)
    public void onEditAboutMeClicked() {
        showAlertDialog();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

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
                aboutUserContentTextView.setText(aboutMePair.second);
            }else {
                //update failed, keep old about me
            }
        });

        mViewModel.getImageUriLiveData().observe(getViewLifecycleOwner(),imageUri->{
            mHandler.postDelayed(() -> {
                profileImage.setImageURI(imageUri);
            },100);
        });

        mViewModel.getUploadingStateLiveData().observe(getViewLifecycleOwner(),isUploading->{
            if (isUploading){
                uploadImageProgressBar.setVisibility(View.VISIBLE);

            }else {
                uploadImageProgressBar.setVisibility(View.GONE);
            }
        });

        mViewModel.getImageUploadSuccessLiveData().observe(getViewLifecycleOwner(),isUploadProfileImageSuccessfullEvent->{
            Boolean isImageUploadSuccessful = isUploadProfileImageSuccessfullEvent.getContentIfNotHandled();
            if (isImageUploadSuccessful!=null && isImageUploadSuccessful){
                //image upload successfully
                Toast.makeText(
                        requireActivity(),
                        getString(R.string.profile_image_updated_successfully),Toast.LENGTH_LONG)
                        .show();
            }else if (isImageUploadSuccessful != null){
                //error uploading image
                Toast.makeText(
                        requireActivity(),
                        getString(R.string.something_went_wrong),Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void setupUi(User user) {
        name.setText(user.getName());
        email.setText(user.getEmail());
        loadPhotoWithShimmerEffect(user);
        setAboutMeText(user);

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            requireActivity().runOnUiThread(() -> {
                //hide place holder loading views and show actual views that has data
                hideLoadingShimmerViews();
                showDataView();
            });
        }, 1500);
    }

    private void setAboutMeText(User user) {
        if (user.getAbout_me()==null){
            aboutUserContentTextView.setText(R.string.not_added_yet);
        }else {
            aboutUserContentTextView.setText(user.getAbout_me());
        }
    }

    private void showDataView() {
        name.setVisibility(View.VISIBLE);
        email.setVisibility(View.VISIBLE);
        followersTextView.setVisibility(View.VISIBLE);
        followingTextView.setVisibility(View.VISIBLE);
        ratingTextView.setVisibility(View.VISIBLE);
        aboutUserContentTextView.setVisibility(View.VISIBLE);
    }

    private void hideLoadingShimmerViews() {
        nameShimmer.setVisibility(View.INVISIBLE);
        emailShimmer.setVisibility(View.INVISIBLE);
        followerCountShimmer.setVisibility(View.INVISIBLE);
        followingCountShimmer.setVisibility(View.INVISIBLE);
        ratingAverageShimmer.setVisibility(View.INVISIBLE);
        aboutUserContentShimmer.setVisibility(View.INVISIBLE);
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
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

    public void loadPhotoWithShimmerEffect(User user){
        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
                .setDuration(1800) // how long the shimmering animation takes to do one full sweep
                .setBaseAlpha(0.7f) //the alpha of the underlying children
                .setHighlightAlpha(0.6f) // the shimmer alpha amount
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .setAutoStart(true)
                .build();

        // This is the placeholder for the imageView
        ShimmerDrawable shimmerDrawable =new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);


        Glide.with(ProfileFragment.this)
                .load(user.getPhoto())
                .apply(new RequestOptions().placeholder(shimmerDrawable).error(R.drawable.anonymous_profile))
                .circleCrop()
                .into(profileImage);
        }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && requestCode == PICK_IMAGE) {
            mViewModel.setImageUri(data.getData());
        }

    }

}
