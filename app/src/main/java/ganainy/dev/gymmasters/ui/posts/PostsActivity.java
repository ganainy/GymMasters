package ganainy.dev.gymmasters.ui.posts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.Post;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.models.app_models.Workout;
import ganainy.dev.gymmasters.ui.findUser.FindUsersActivity;
import ganainy.dev.gymmasters.ui.posts.postComments.PostCommentsFragment;
import ganainy.dev.gymmasters.utils.ApplicationViewModelFactory;
import ganainy.dev.gymmasters.utils.NetworkChangeReceiver;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ganainy.dev.gymmasters.utils.NetworkUtil;

public class PostsActivity extends AppCompatActivity {
    private static final String TAG = "PostsActivity";
    private NetworkChangeReceiver networkChangeReceiver;
    private PostsViewModel mViewModel;
    private PostsAdapter postsAdapter;

    @BindView(R.id.noNewsFeedTextView)
    TextView noNewsFeedTextView;

    @BindView(R.id.errorTextView)
    TextView errorTextView;

    @BindView(R.id.findUsersButton)
    Button findUsersButton;

    @BindView(R.id.loadingGroup)
    Group loadingGroup;

    @BindView(R.id.sharedRv)
    RecyclerView recyclerView;

    @OnClick(R.id.findUsersButton)
    void openFindUsers() {
        Intent i = new Intent(PostsActivity.this, FindUsersActivity.class);
        i.putExtra("source", "find");
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        ButterKnife.bind(this);

        setupRecycler();
        initViewModel();

        mViewModel.getFollowingUid();

        mViewModel.getNetworkStateLiveData().observe(this, networkState -> {
            switch (networkState) {
                case SUCCESS:
                    errorTextView.setVisibility(View.GONE);
                    noNewsFeedTextView.setVisibility(View.GONE);
                    findUsersButton.setVisibility(View.GONE);
                    loadingGroup.setVisibility(View.GONE);
                    break;
                case ERROR:
                    errorTextView.setVisibility(View.VISIBLE);
                    noNewsFeedTextView.setVisibility(View.GONE);
                    findUsersButton.setVisibility(View.GONE);
                    loadingGroup.setVisibility(View.GONE);
                    break;
                case LOADING:
                    errorTextView.setVisibility(View.GONE);
                    noNewsFeedTextView.setVisibility(View.GONE);
                    findUsersButton.setVisibility(View.GONE);
                    loadingGroup.setVisibility(View.VISIBLE);
                    break;
                case EMPTY:
                    errorTextView.setVisibility(View.GONE);
                    noNewsFeedTextView.setVisibility(View.VISIBLE);
                    findUsersButton.setVisibility(View.VISIBLE);
                    loadingGroup.setVisibility(View.GONE);
                    break;
                case STOP_LOADING:
                    break;
            }

        });


        mViewModel.getPostListLiveData().observe(this, posts -> {

                        Collections.sort(posts, (s1, s2) -> s2.getDateStamp().compareTo(s1.getDateStamp()));
            postsAdapter.setData(posts);
            postsAdapter.notifyDataSetChanged();
        });

        /*called when we need to update single recycler item, for example when post is liked*/
        mViewModel.getUpdatePostLiveData().observe(this,postsEvent->{
            Pair<List<Post>, Integer> postsPositionPair = postsEvent.getContentIfNotHandled();
            if (postsPositionPair!=null){
                postsAdapter.setData(postsPositionPair.first);
                postsAdapter.notifyItemChanged(postsPositionPair.second);
            }
        });
    }

    private void initViewModel() {
        ApplicationViewModelFactory applicationViewModelFactory = new ApplicationViewModelFactory(getApplication());
        mViewModel = new ViewModelProvider(this, applicationViewModelFactory).get(PostsViewModel.class);
    }


    private void setupRecycler() {

        postsAdapter = new PostsAdapter(getApplication(), new PostCallback() {
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
        PostCommentsFragment postCommentsFragment = PostCommentsFragment.newInstance(post);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, postCommentsFragment).addToBackStack("postCommentsFragment").commit();
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


    /**
     * handle back press from toolbar
     */
    @OnClick(R.id.backArrowImageView)
    public void onViewClicked() {
        onBackPressed();
    }


}
