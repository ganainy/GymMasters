package ganainy.dev.gymmasters.ui.posts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.Post;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.models.app_models.Workout;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ganainy.dev.gymmasters.ui.main.ActivityCallback;
import ganainy.dev.gymmasters.utils.AuthUtils;

public class PostsFragment extends Fragment {
    private static final String TAG = "PostsFragment";
    public static final String SOURCE = "source";
    public static final String FIND = "find";
    private PostsViewModel mViewModel;
    private PostsAdapter postsAdapter;


    @BindView(R.id.errorTextView)
    TextView errorTextView;

    @BindView(R.id.empty_posts_layout)
    ConstraintLayout emptyPostsLayout;

    @BindView(R.id.loadingGroup)
    Group loadingGroup;

    @BindView(R.id.sharedRv)
    RecyclerView recyclerView;

    @OnClick(R.id.findUsersButton)
    void openFindUsers() {
        ActivityCallback activityCallback =(ActivityCallback) requireActivity();
        activityCallback.onOpenFindUsersActivity(SOURCE, FIND);
    }


    public static PostsFragment newInstance() {
        return new PostsFragment();
    }

    public PostsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.posts_fragment, container, false);
        ButterKnife.bind(this, view);
        setupRecycler();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(PostsViewModel.class);

        saveLoggedUser();

        mViewModel.getFollowingUid();

        mViewModel.getNetworkStateLiveData().observe(getViewLifecycleOwner(), networkState -> {
            switch (networkState) {
                case SUCCESS:
                    errorTextView.setVisibility(View.GONE);
                    emptyPostsLayout.setVisibility(View.GONE);
                    loadingGroup.setVisibility(View.GONE);
                    break;
                case ERROR:
                    errorTextView.setVisibility(View.VISIBLE);
                    emptyPostsLayout.setVisibility(View.GONE);
                    loadingGroup.setVisibility(View.GONE);
                    break;
                case LOADING:
                    errorTextView.setVisibility(View.GONE);
                    emptyPostsLayout.setVisibility(View.GONE);
                    loadingGroup.setVisibility(View.VISIBLE);
                    break;
                case EMPTY:
                    errorTextView.setVisibility(View.GONE);
                    emptyPostsLayout.setVisibility(View.VISIBLE);
                    loadingGroup.setVisibility(View.GONE);
                    break;
                case STOP_LOADING:
                    break;
            }

        });


        mViewModel.getPostListLiveData().observe(getViewLifecycleOwner(), posts -> {

            Collections.sort(posts, (s1, s2) -> s2.getDateStamp().compareTo(s1.getDateStamp()));
            postsAdapter.setData(posts);
            postsAdapter.notifyDataSetChanged();
        });

        /*called when we need to update single recycler item, for example when post is liked*/
        mViewModel.getUpdatePostLiveData().observe(getViewLifecycleOwner(),postsEvent->{
            Pair<List<Post>, Integer> postsPositionPair = postsEvent.getContentIfNotHandled();
            if (postsPositionPair!=null){
                postsAdapter.setData(postsPositionPair.first);
                postsAdapter.notifyItemChanged(postsPositionPair.second);
            }
        });
    }

    /**save logged user info to be accessed through app*/
    private void saveLoggedUser() {
        mViewModel.getLoggedUser().observe(getViewLifecycleOwner(),loggedUser->{
            AuthUtils.putUser(requireContext(),loggedUser);
        });
    }

    private void setupRecycler() {

        postsAdapter = new PostsAdapter(requireActivity().getApplication(), new PostCallback() {
            @Override
            public void onExerciseClicked(Exercise exercise, Integer adapterPosition) {
                //todo open selected exercise
               /* Intent i = new Intent(this,);
                i.putExtra("exercise", currentExercise);
                startActivity(i);*/
            }

            @Override
            public void onWorkoutClicked(Workout workout, Integer adapterPosition) {

            }

            @Override
            public void onUserClicked(User user) {

            }

            @Override
            public void onPostLike(Post post, Integer adapterPosition) {
                mViewModel.likePost(post, adapterPosition);
            }

            @Override
            public void onPostComment(Post post, Integer postType) {
                    openPostCommentFragment(post);
            }
        });
        //sharedExerciseWorkoutList setdata

        postsAdapter.setHasStableIds(true);
        recyclerView.setItemAnimator(null);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(postsAdapter);


    }

    private void openPostCommentFragment(Post post) {
        ActivityCallback activityCallback=(ActivityCallback) requireActivity();
        activityCallback.onOpenPostCommentFragment(post);
  }

}
