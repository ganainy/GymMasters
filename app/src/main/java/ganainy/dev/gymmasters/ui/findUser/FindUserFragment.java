package ganainy.dev.gymmasters.ui.findUser;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.ui.main.ActivityCallback;
import ganainy.dev.gymmasters.utils.NetworkChangeReceiver;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FindUserFragment extends Fragment  {
    private static final String TAG = FindUserFragment.class.getSimpleName();
    public static final String SOURCE = "source";
    public static final String ALL = "all";
    public static final String FOLLOWERS = "followers";
    public static final String FOLLOWING = "following";

    /*this can be used to show all users/ followers of logged user or people who logged user is
     following*/
    List<User> users = new ArrayList<>();
    List<User> filteredUsers = new ArrayList<>();

    private UserAdapter userAdapter;
    private FindUserViewModel mViewModel;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.noNewsFeedTextView)
    TextView notFoundTextView;

    @BindView(R.id.loadingProgressbar)
    ProgressBar loadingProgressbar;

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
    void showSearchView() {
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
    void filterUsers() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filteredUsers.clear();
                for (User user : users) {
                    if (user.getName().contains(s)) {
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
        requireActivity().onBackPressed();
    }


    public static FindUserFragment newInstance(String filterType) {
        FindUserFragment findUserFragment = new FindUserFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SOURCE, filterType);
        findUserFragment.setArguments(bundle);
        return findUserFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(FindUserViewModel.class);

        //this fragment called from more than one source so we differ with SOURCE value
        if (getArguments()!=null && getArguments().getString(SOURCE) != null) {
            switch (getArguments().getString(SOURCE)) {
                case ALL:
                    titleTextView.setText(R.string.users_list);
                    mViewModel.loadAllUsers();
                    break;
                case FOLLOWERS:
                    titleTextView.setText(R.string.my_followers);
                    mViewModel.loadFollowersIds();
                    break;
                case FOLLOWING:
                    titleTextView.setText(R.string.users_iam_following);
                    mViewModel.loadFollowingId();
                    break;
            }
        }

        mViewModel.getNoUsersLiveData().observe(getViewLifecycleOwner(), noUserType -> {
            switch (noUserType) {

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


        mViewModel.followingUserTransformation.observe(getViewLifecycleOwner(), followingUser -> {
            //must subscribe to trigger transformation
        });

        mViewModel.followerUserTransformation.observe(getViewLifecycleOwner(), followingUser -> {
            //must subscribe to trigger transformation
        });

        mViewModel.userWithRatingTransformation.observe(getViewLifecycleOwner(), userWithRating -> {
            //must subscribe to trigger transformation
        });

        mViewModel.userWithRatingAndFollowerCountTransformation.observe(getViewLifecycleOwner(), userWithRatingAndFollowerCount -> {
            Log.d(TAG, "userWithRatingAndFollowerCountTransformation: " + userWithRatingAndFollowerCount);
            users.add(userWithRatingAndFollowerCount);
            userAdapter.setData(users);
            userAdapter.notifyItemInserted(users.size() - 1);
        });

        mViewModel.getLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading)
                loadingProgressbar.setVisibility(View.VISIBLE);
            else
                loadingProgressbar.setVisibility(View.GONE);
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find_users_fragment, container, false);
        ButterKnife.bind(this, view);

        setupRecycler();
        return view;
    }

    private void setupRecycler() {
        userAdapter = new UserAdapter(requireActivity(), (user, adapterPosition) -> {
            ((ActivityCallback)requireActivity()).onOpenUserFragment(user);
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

}

