package ganainy.dev.gymmasters.ui.findUser;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.ui.userExercises.UserExercisesFragment;
import ganainy.dev.gymmasters.ui.main.ActivityCallback;
import ganainy.dev.gymmasters.ui.main.loggedUserWorkouts.UserWorkoutsFragment;
import ganainy.dev.gymmasters.ui.specificExercise.ExerciseFragment;
import ganainy.dev.gymmasters.ui.userInfo.UserInfoFragment;
import ganainy.dev.gymmasters.utils.ApplicationViewModelFactory;
import ganainy.dev.gymmasters.utils.NetworkChangeReceiver;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ganainy.dev.gymmasters.utils.NetworkUtil;

public class FindUsersActivity extends AppCompatActivity implements ActivityCallback {
    private static final String TAG = "FindUsersActivity";
    public static final String SOURCE = "source";
    public static final String FIND = "find";
    public static final String FOLLOWERS = "followers";
    public static final String FOLLOWING = "following";
    public static final String USER = "user";

    /*this can be used to show all users/ followers of logged user or people who logged user is
     following*/
    List<User> users=new ArrayList<>();
    List<User> filteredUsers=new ArrayList<>();


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private UserAdapter userAdapter;
    private NetworkChangeReceiver networkChangeReceiver;
    private FindUserViewModel mViewModel;

    @BindView(R.id.notFoundTextView)
    TextView notFoundTextView;

    @BindView(R.id.findUsersButton)
    Button findButton;

    @BindView(R.id.bgImageView)
    ImageView bgImageView;

    @BindView(R.id.usersRecycler)
    RecyclerView recyclerView;

    @BindView(R.id.no_users_layout)
    ConstraintLayout noUsersLayout;

    @BindView(R.id.no_followers_layout)
    ConstraintLayout noFollowersLayout;

    @BindView(R.id.no_following_layout)
    ConstraintLayout noFollowingLayout;

    @BindView(R.id.titleTextView)
    TextView titleTextView;

    @BindView(R.id.searchView)
    SearchView searchView;

    @BindView(R.id.spacer)
    Space spacer;

    @BindView(R.id.backArrowImageView)
    ImageView backArrowImageView;

    @BindView(R.id.filterImageView)
    ImageView filterImageView;

    @OnClick(R.id.filterImageView)
    void showSearchView(){
        showSearchViewLayout();
    }

    private void showSearchViewLayout() {
        filterImageView.setVisibility(View.GONE);
        backArrowImageView.setVisibility(View.GONE);
        spacer.setVisibility(View.GONE);
        titleTextView.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.searchView)
    void filterUsers(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filteredUsers.clear();
                for (User user:users){
                    if (user.getName().contains(s)){
                        filteredUsers.add(user);
                    }
                }
                userAdapter.setData(filteredUsers);
                userAdapter.notifyDataSetChanged();
                return true;
            }
        });

        searchView.setOnCloseListener(() -> {
            hideSearchViewLayout();
            return false;
        });
    }

    private void hideSearchViewLayout() {
        filterImageView.setVisibility(View.VISIBLE);
        backArrowImageView.setVisibility(View.VISIBLE);
        spacer.setVisibility(View.VISIBLE);
        titleTextView.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.GONE);
    }

    @OnClick(R.id.backArrowImageView)
    void onBackArrowClick() {
        onBackPressed();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_users);
        ButterKnife.bind(this);

        //todo handle loading layout and pagination
        setupRecycler();
        initViewModel();

        //this activity called from more than one source so we differ with intent param
        if (getIntent().getStringExtra(SOURCE).equals(FIND)) {
            titleTextView.setText(R.string.users_list);
            mViewModel.loadAllUsers();
        }
        else if (getIntent().getStringExtra(SOURCE).equals(FOLLOWERS)) {
            titleTextView.setText(R.string.my_followers);
            mViewModel.loadFollowersIds();
        } else if (getIntent().getStringExtra(SOURCE).equals(FOLLOWING)) {
            titleTextView.setText(R.string.users_iam_following);
          mViewModel.loadFollowingId();
        }

        mViewModel.getNoUsersLiveData().observe(this,noUserType->{
            switch (noUserType){

                case NO_FOLLOWERS:
                    noUsersLayout.setVisibility(View.GONE);
                    noFollowersLayout.setVisibility(View.VISIBLE);
                    noFollowingLayout.setVisibility(View.GONE);
                    break;
                case NO_FOLLOWING:
                    noUsersLayout.setVisibility(View.GONE);
                    noFollowersLayout.setVisibility(View.GONE);
                    noFollowingLayout.setVisibility(View.VISIBLE);
                    break;
                case NO_USERS:
                    noUsersLayout.setVisibility(View.VISIBLE);
                    noFollowersLayout.setVisibility(View.GONE);
                    noFollowingLayout.setVisibility(View.GONE);
                    break;
            }
        });


        mViewModel.followingUserTransformation.observe(this, followingUser->{
            //must subscribe to trigger transformation
        });

        mViewModel.followerUserTransformation.observe(this, followingUser->{
            //must subscribe to trigger transformation
        });

        mViewModel.userWithRatingTransformation.observe(this, userWithRating->{
            //must subscribe to trigger transformation
        });

        mViewModel.userWithRatingAndFollowerCountTransformation.observe(this, userWithRatingAndFollowerCount->{
            Log.d(TAG, "userWithRatingAndFollowerCountTransformation: "+userWithRatingAndFollowerCount);
            users.add(userWithRatingAndFollowerCount);
            userAdapter.setData(users);
            userAdapter.notifyItemInserted(users.size()-1);
        });


    }

    private void initViewModel() {
        ApplicationViewModelFactory applicationViewModelFactory = new ApplicationViewModelFactory(getApplication());
        mViewModel = new ViewModelProvider(this, applicationViewModelFactory).get(FindUserViewModel.class);
    }


    private void setupRecycler() {
        userAdapter = new UserAdapter(FindUsersActivity.this, (user, adapterPosition) -> {
            UserInfoFragment userInfoFragment = UserInfoFragment.newInstance(user);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.container, userInfoFragment).addToBackStack("userInfoFragment").commit();
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
               /* int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if(lastVisibleItemPosition>=users.size()-1){
                    Log.d(TAG, "onScrolled: "+lastVisibleItemPosition);
                    mViewModel.getMoreUsers(users.get(users.size()-1).getId());
                }*/
            }
        });
        recyclerView.setAdapter(userAdapter);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    protected void onStop() {
        super.onStop();
        NetworkUtil.unregisterNetworkReceiver(this, networkChangeReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        networkChangeReceiver = NetworkUtil.registerNetworkReceiver(this);
    }


    /*override methods called by userinfo fragment to show certain user exercises/workouts*/
    @Override
    public void openUserWorkoutsFragment(String userId, String userName) {
        UserWorkoutsFragment userWorkoutsFragment = UserWorkoutsFragment.newInstance(userId,userName);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, userWorkoutsFragment).addToBackStack("userWorkoutsFragment").commit();
    }

    @Override
    public void openUserExercisesFragment(String userId, String userName) {
        UserExercisesFragment userExercisesFragment = UserExercisesFragment.newInstance(userId,userName);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, userExercisesFragment).addToBackStack("userExercisesFragment").commit();
    }

    @Override
    public void openCreateWorkoutFragment() {

    }

    @Override
    public void openCreateExerciseFragment() {

    }

    @Override
    public void openExerciseFragment(Exercise exercise) {
        ExerciseFragment exerciseFragment = ExerciseFragment.newInstance(exercise.getName(),exercise.getBodyPart());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, exerciseFragment).addToBackStack("exerciseFragment").commit();
    }
}

