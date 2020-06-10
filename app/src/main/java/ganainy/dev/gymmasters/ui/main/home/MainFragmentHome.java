package ganainy.dev.gymmasters.ui.main.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.shared_adapters.ExerciseAdapter;
import ganainy.dev.gymmasters.shared_adapters.WorkoutAdapter;
import ganainy.dev.gymmasters.ui.createWorkout.CreateWorkoutFragment;
import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.models.app_models.Workout;
import ganainy.dev.gymmasters.ui.createExercise.CreateNewExerciseActivity;
import ganainy.dev.gymmasters.ui.main.MainActivity;
import ganainy.dev.gymmasters.utils.MyConstant;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.auth.FirebaseAuth;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainFragmentHome extends Fragment {
    private static final String TAG = "MainFragmentHome";
    ScrollView scrollView;
    ExerciseAdapter exerciseAdapter;
    @BindView(R.id.viewMyExercisesButton)
    Button viewMyExercisesButton;
    @BindView(R.id.viewMyWorkoutsButton)
    Button viewMyWorkoutsButton;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.about_me_text)
    TextView aboutMeText;
    @BindView(R.id.profile_image)
    ImageView profileImage;
    @BindView(R.id.followersTextView)
    TextView followersTextView;
    @BindView(R.id.followingTextView)
    TextView followingTextView;
    @BindView(R.id.ratingTextView)
    TextView ratingTextView;
    private View view;
    /**
     * 1 shown ,0 didnt load ,2 hidden
     */
    private int exerciseFlag = 0;
    private int workoutFlag = 0;
    private RecyclerView recyclerViewExercise, recyclerViewWorkout;
    private MainFragmentHomeViewModel mainFragmentHomeViewModel;
    private Button saveAlertDialog, cancelAlertDialog;


    @OnClick(R.id.createWorkout)
    void createWorkout() {
        CreateWorkoutFragment createWorkoutFragment = new CreateWorkoutFragment();
        MainActivity mainActivity = (MainActivity) getActivity();
        FragmentTransaction fragmentTransaction = mainActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.parent_container, createWorkoutFragment).addToBackStack("createWorkoutFragment").commit();
    }

    private WorkoutAdapter workoutAdapter;

    @OnClick(R.id.createExercise)
    void createExercise() {
        startActivity(new Intent(getActivity(), CreateNewExerciseActivity.class));
    }

    @OnClick(R.id.viewMyExercisesButton)
    void viewExercises() {

        if (exerciseFlag == 1) {
            /**it means exercise is shown and i should hide them*/
            recyclerViewExercise.setVisibility(View.GONE);
            exerciseFlag = 2;
        } else if (exerciseFlag == 2) {
            /**recycler is hidden and i should show it*/
            exerciseFlag = 1;
            recyclerViewExercise.setVisibility(View.VISIBLE);
            //move screen to recycler
            recyclerViewExercise.requestFocus();
        } else if (exerciseFlag == 0) {
            /**didn't download exercises yet*/
            mainFragmentHomeViewModel.downloadMyExercises().observe(this, new Observer<List<Exercise>>() {
                @Override
                public void onChanged(List<Exercise> exercises) {
                    setupExercisesRecycler(exercises);
                }
            });
        }
    }

    @OnClick(R.id.viewMyWorkoutsButton)
    void viewWorkouts() {
        if (workoutFlag == 1) {
            /**it means exercise is shown and i should hide them*/
            recyclerViewWorkout.setVisibility(View.GONE);
            workoutFlag = 2;
        } else if (workoutFlag == 2) {
            /**recycler is hidden and i should show it*/
            workoutFlag = 1;
            recyclerViewWorkout.setVisibility(View.VISIBLE);
            //move screen to recycler
            recyclerViewWorkout.requestFocus();
        } else if (workoutFlag == 0) {
            /**didn't download exercises yet*/
            mainFragmentHomeViewModel.downloadMyWorkout().observe(this, new Observer<List<Workout>>() {
                @Override
                public void onChanged(List<Workout> workouts) {
                    setupWorkoutRecycler(workouts);
                }
            });
        }


    }


    public MainFragmentHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main_fragment_home, container, false);
        ButterKnife.bind(this, view);

        recyclerViewWorkout = view.findViewById(R.id.customWorkoutRecycler);
        recyclerViewExercise = view.findViewById(R.id.customExerciseRecycler);

        scrollView = view.findViewById(R.id.scrollView);

        mainFragmentHomeViewModel = ViewModelProviders.of(this).get(MainFragmentHomeViewModel.class);

        setLoggedInUserId();


        return view;
    }

    private void setLoggedInUserId() {
        if (FirebaseAuth.getInstance().getUid() != null) {
            MyConstant.loggedInUserId = FirebaseAuth.getInstance().getUid();
        } else if (GoogleSignIn.getLastSignedInAccount(getActivity()) != null) {
            MyConstant.loggedInUserId = GoogleSignIn.getLastSignedInAccount(getActivity()).getId();
        }
        setUserData(MyConstant.loggedInUserId);

    }

    private void setUserData(String id) {
        mainFragmentHomeViewModel.getUserData(id).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                name.setText(user.getName());
                email.setText(user.getEmail());
                aboutMeText.setText(user.getAbout_me());
                mainFragmentHomeViewModel.downloadUserPhoto(user.getPhoto()).observe(MainFragmentHome.this, new Observer<Uri>() {
                    @Override
                    public void onChanged(Uri uri) {
                        Glide.with(MainFragmentHome.this).load(uri).into(profileImage);
                    }
                });
            }
        });


        mainFragmentHomeViewModel.getFollowersCount().observe(MainFragmentHome.this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                followersTextView.setText(s);
            }
        });

        mainFragmentHomeViewModel.getFollowingCount().observe(MainFragmentHome.this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                followingTextView.setText(s);
            }
        });


        mainFragmentHomeViewModel.getRatingsAvg().observe(MainFragmentHome.this, new Observer<Long>() {
            @Override
            public void onChanged(Long l) {
                ratingTextView.setText(l + "/5");
            }
        });
    }


    private void setupExercisesRecycler(final List<Exercise> exercises) {
        if (exercises.size() == 0) {
            FancyToast.makeText(getActivity(), "You didn't create any custom exercises yet.", FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();
        } else {


            exerciseFlag = 1;
            recyclerViewExercise.setVisibility(View.VISIBLE);
            exerciseAdapter = new ExerciseAdapter(getActivity(), exercises, "home");
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerViewExercise.setLayoutManager(linearLayoutManager);
            recyclerViewExercise.setAdapter(exerciseAdapter);


            //move screen to recycler
            recyclerViewExercise.requestFocus();

        }

    }



    private void setupWorkoutRecycler(List<Workout> workouts) {
        if (workouts.size() == 0) {
            FancyToast.makeText(getActivity(), "You didn't create any custom workouts yet.", FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();
        } else {
            workoutFlag = 1;
            recyclerViewWorkout.setVisibility(View.VISIBLE);
            workoutAdapter = new WorkoutAdapter(getActivity(), "fragmentHome");
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerViewWorkout.setLayoutManager(linearLayoutManager);
            workoutAdapter.setDataSource(workouts);
            recyclerViewWorkout.setAdapter(workoutAdapter);

            //move screen to recycler
            recyclerViewWorkout.requestFocus();
        }

    }


    @OnClick(R.id.edit)
    public void onViewClicked() {
        showAlertDialog();
    }

    private void showAlertDialog() {

        final View alertDialogView = getLayoutInflater().inflate(R.layout.edit_view, null);


        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()
        )

                .setView(alertDialogView)
                .show();


        saveAlertDialog = alertDialogView.findViewById(R.id.save_action);
        cancelAlertDialog = alertDialogView.findViewById(R.id.cancel_action);


        saveAlertDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = alertDialogView.findViewById(R.id.editText);
                final String s = editText.getText().toString();
                updateAboutMe(s);
                alertDialog.dismiss();
            }
        });

        cancelAlertDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

    }

    private void updateAboutMe(final String s) {
        mainFragmentHomeViewModel.updateAboutMe(s).observe(MainFragmentHome.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    FancyToast.makeText(getActivity(), "Update successful.", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                    aboutMeText.setText(s);

                } else {
                    FancyToast.makeText(getActivity(), "Update failed.\n an error occured", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        recyclerViewWorkout.setAdapter(null);
        recyclerViewExercise.setAdapter(null);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        if (workoutAdapter != null) recyclerViewWorkout.setAdapter(workoutAdapter);
        if (exerciseAdapter != null) recyclerViewExercise.setAdapter(exerciseAdapter);
    }
}
