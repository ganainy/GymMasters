package ganainy.dev.gymmasters.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.Post;
import ganainy.dev.gymmasters.models.app_models.Workout;
import ganainy.dev.gymmasters.ui.createExercise.CreateExerciseFragment;
import ganainy.dev.gymmasters.ui.createWorkout.CreateWorkoutFragment;
import ganainy.dev.gymmasters.ui.findUser.FindUsersActivity;
import ganainy.dev.gymmasters.ui.main.home.ProfileFragment;
import ganainy.dev.gymmasters.ui.map.MapsActivity;
import ganainy.dev.gymmasters.ui.posts.postComments.PostCommentsFragment;
import ganainy.dev.gymmasters.ui.userExercises.UserExercisesFragment;
import ganainy.dev.gymmasters.ui.main.loggedUserWorkouts.UserWorkoutsFragment;
import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.ui.specificExercise.ExerciseFragment;
import ganainy.dev.gymmasters.ui.specificExercise.youtubeFragment.YoutubeFragment;
import ganainy.dev.gymmasters.ui.login.LoginActivity;
import ganainy.dev.gymmasters.ui.workout.WorkoutFragment;
import ganainy.dev.gymmasters.utils.AuthUtils;
import ganainy.dev.gymmasters.utils.NetworkChangeReceiver;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import ganainy.dev.gymmasters.utils.NetworkUtil;

public class MainActivity extends AppCompatActivity implements ActivityCallback {
    public static final String SOURCE = "source";
    public static final String FOLLOWERS = "followers";
    public static final String FIND = "find";
    public static final String FOLLOWING = "following";
    public NetworkChangeReceiver networkChangeReceiver;
    private static final String TAG = "MainActivity";

    @BindView(R.id.view_pager_main)
    ViewPager view_pager_main;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.container)
    FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        setupViewPager();

        setupDrawerLayout();

        //handle click on navigation view items
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_discover:
                    handleDiscoverClick();
                    break;
                case R.id.nav_profile:
                    handleProfileClick();
                    break;
                case R.id.nav_map:
                    handleMapClick();
                    break;
                case R.id.nav_sign_out:
                    handleSignOutClick();
                    break;
            }
            return false;
        });
    }

    private void setupDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void handleMapClick() {
        drawerLayout.closeDrawers();
        startActivity(new Intent(this, MapsActivity.class));
    }

    private void handleSignOutClick() {
        drawerLayout.closeDrawers();
        //todo move to viewmodel
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //user logged in with email/password
            mAuth.signOut();
            AuthUtils.setLoggedUserId(null);
            openLoginActivityAndClearTask();
        } else {
            //user logged in with google account
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    openLoginActivityAndClearTask();
                    AuthUtils.setLoggedUserId(null);
                }
                else {
                    Toast.makeText(
                            MainActivity.this,
                            getString(R.string.error_logging_out) + task.getException(),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }

    private void handleDiscoverClick() {
        drawerLayout.closeDrawers();
        Intent i = new Intent(MainActivity.this, FindUsersActivity.class);
        i.putExtra(SOURCE, FIND);
        startActivity(i);
    }

    private void handleProfileClick() {
        drawerLayout.closeDrawers();
        ProfileFragment profileFragment = ProfileFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, profileFragment).addToBackStack("profileFragment").commit();
    }

    private void setupViewPager() {
        //setup viewPager and connect it with tablayout
        view_pager_main.setAdapter(new ViewPagerAdapterMainActivity(getSupportFragmentManager()));

        tabLayout.setupWithViewPager(view_pager_main, true);

        setupTabLayoutIconsAndLabels();
    }

    private void setupTabLayoutIconsAndLabels() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_blog_black);
        tabLayout.getTabAt(0).setText(getString(R.string.feed));
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_dumbell_grey);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_workout_grey);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        tab.setText(getString(R.string.feed));
                        tab.setIcon(R.drawable.ic_blog_black);
                        break;
                    case 1:
                        tab.setText(getString(R.string.exercises));
                        tab.setIcon(R.drawable.ic_dumbell_black);
                        break;
                    case 2:
                        tab.setText(getString(R.string.workouts));
                        tab.setIcon(R.drawable.ic_workout_black);
                        break;
                }
                tab.setTabLabelVisibility(TabLayout.TAB_LABEL_VISIBILITY_LABELED);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setTabLabelVisibility(TabLayout.TAB_LABEL_VISIBILITY_UNLABELED);
                switch (tab.getPosition()) {
                    case 0:
                        tab.setIcon(R.drawable.ic_blog_grey);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.ic_dumbell_grey);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.ic_workout_grey);
                        break;
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    private void openLoginActivityAndClearTask() {
        //finish this activity on log out so users won't be able to log in on back press in login
        Intent openLoginInNewTaskIntent = new Intent(getApplicationContext(), LoginActivity.class);
        openLoginInNewTaskIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(openLoginInNewTaskIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        /*when back pressed on main activity close drawer if open else show dialog to confirm
        closing app*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            /*if there is fragment in back stack close it only*/
            super.onBackPressed();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.are_you_sure_exit)
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, (arg0, arg1) -> {
                        // Finish this activity as well as all activities immediately below it
                        finishAffinity();
                    })
                    .create().show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
     NetworkUtil.unregisterNetworkReceiver(this,networkChangeReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
         networkChangeReceiver = NetworkUtil.registerNetworkReceiver(this);
    }

    /**methods called by child fragments to talk to parent activity*/
    @Override
    public void openUserWorkoutsFragment(String userId,String userName) {
        UserWorkoutsFragment userWorkoutsFragment = UserWorkoutsFragment.newInstance(userId,null);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, userWorkoutsFragment).addToBackStack("userWorkoutsFragment").commit();
    }

    @Override
    public void openUserExercisesFragment(String userId,String userName) {
        UserExercisesFragment userExercisesFragment = UserExercisesFragment.newInstance(userId,null);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, userExercisesFragment).addToBackStack("userExercisesFragment").commit();
    }

    @Override
    public void openCreateWorkoutFragment() {
        CreateWorkoutFragment createWorkoutFragment = new CreateWorkoutFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, createWorkoutFragment).addToBackStack("createWorkoutFragment").commit();
    }

    @Override
    public void openCreateExerciseFragment() {
        CreateExerciseFragment createExerciseFragment = new CreateExerciseFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, createExerciseFragment).addToBackStack("createExerciseFragment").commit();
    }

    @Override
    public void openExerciseFragment(Exercise exercise) {
        ExerciseFragment exerciseFragment = ExerciseFragment.newInstance(exercise);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, exerciseFragment).addToBackStack("exerciseFragment").commit();
    }

    @Override
    public void showLoggedUserFollowers(String key, String value) {
        Intent i = new Intent(MainActivity.this, FindUsersActivity.class);
        i.putExtra(key, value);
        startActivity(i);
    }

    @Override
    public void showUsersFollowedByLoggedUser(String key, String value) {
        Intent i = new Intent(MainActivity.this, FindUsersActivity.class);
        i.putExtra(key, value);
        startActivity(i);
    }

    @Override
    public void openYoutubeFragment(String exerciseName) {
        YoutubeFragment youtubeFragment = YoutubeFragment.newInstance(exerciseName);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, youtubeFragment).addToBackStack("youtubeFragment").commit();
    }

    @Override
    public void onOpenFindUsersActivity(String key, String value) {
        Intent i = new Intent(this, FindUsersActivity.class);
        i.putExtra(key, value);
        startActivity(i);
    }

    @Override
    public void onOpenPostCommentFragment(Post post) {
        PostCommentsFragment postCommentsFragment = PostCommentsFragment.newInstance(post);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, postCommentsFragment).addToBackStack("postCommentsFragment").commit();
    }

    @Override
    public void onOpenWorkoutFragment(Workout workout) {
        WorkoutFragment workoutFragment = WorkoutFragment.newInstance(workout);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, workoutFragment).addToBackStack("workoutFragment").commit();
    }
}
