package ganainy.dev.gymmasters.ui.posts.postComments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.Post;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.models.app_models.Workout;
import ganainy.dev.gymmasters.ui.posts.PostCallback;

public class PostCommentsFragment extends Fragment {
    public static final String TAG = "PostCommentsFragment";

    PostCommentsAdapter postCommentsAdapter;

    @BindView(R.id.commentEditText)
    EditText commentEditText;

    @BindView(R.id.recycler_view)
    RecyclerView commentsRecyclerView;

    @BindView(R.id.sendImageView)
    ImageView sendImageView;

    public static final String POST = "post";
    private PostCommentsViewModel mViewModel;

    public static PostCommentsFragment newInstance(Post post) {
        PostCommentsFragment exercisePostCommentsFragment = new PostCommentsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(POST, post);
        exercisePostCommentsFragment.setArguments(bundle);
        return exercisePostCommentsFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.workout_post_comments_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(PostCommentsViewModel.class);
        initRecycler();
        initListeners();

        if (savedInstanceState==null){
            //only download data if this fragment is NOT recreated
            Post post = getArguments().getParcelable(POST);
            mViewModel.setPost(post);
            mViewModel.getPostComments();
        }


        mViewModel.getPostCommentListLiveData().observe(getViewLifecycleOwner(), postCommentList -> {
                postCommentsAdapter.setData(postCommentList);
                postCommentsAdapter.notifyDataSetChanged();
        });


        mViewModel.getUpdatedPostCommentsLiveData().observe(getViewLifecycleOwner(), updatedPostCommentList->{
            postCommentsAdapter.setData(updatedPostCommentList);
           postCommentsAdapter.notifyItemInserted(commentsRecyclerView.getAdapter().getItemCount());
           commentsRecyclerView.smoothScrollToPosition(commentsRecyclerView.getAdapter().getItemCount());
        });

        mViewModel.getUpdatedPostLikesLiveData().observe(getViewLifecycleOwner(), updatedPostLikesList->{
            postCommentsAdapter.setData(updatedPostLikesList);
            postCommentsAdapter.notifyItemChanged(0);
        });
    }

    private void initRecycler() {

        postCommentsAdapter = new PostCommentsAdapter(getActivity().getApplication(), new PostCallback() {
            @Override
            public void onExerciseClicked(Exercise exercise, Integer adapterPosition) {
                //todo
            }

            @Override
            public void onWorkoutClicked(Workout workout, Integer adapterPosition) {

            }

            @Override
            public void onUserClicked(String postCreatorId) {

            }

            @Override
            public void onPostLike(Post post, Integer adapterPosition) {
                mViewModel.likePost();
            }

            @Override
            public void onPostComment(Post post, Integer adapterPosition) {
                commentEditText.requestFocus();
            }
        });
        commentsRecyclerView.setItemAnimator(null);
        commentsRecyclerView.setAdapter(postCommentsAdapter);
    }

    public void initListeners() {

        //softkeyboard send action on keyboard does same job as send button
        commentEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                mViewModel.saveComment(commentEditText.getText().toString());
                commentEditText.setText("");
            }
            return false;
        });

        sendImageView.setOnClickListener(v -> {
            if (commentEditText.getText().toString().trim().isEmpty()) {
                commentEditText.startAnimation(shakeError());
            } else {
                mViewModel.saveComment(commentEditText.getText().toString());
                commentEditText.setText("");
            }
        });
            }

    public TranslateAnimation shakeError() {
        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(7));
        return shake;
    }



}