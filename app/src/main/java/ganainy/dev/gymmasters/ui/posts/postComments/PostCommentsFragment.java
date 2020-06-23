package ganainy.dev.gymmasters.ui.posts.postComments;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.Post;
import ganainy.dev.gymmasters.utils.AuthUtils;

public class PostCommentsFragment extends Fragment implements CommentCallback {
    public static final String TAG = "PostCommentsFragment";

    PostCommentsAdapter postCommentsAdapter;

    @BindView(R.id.postExerciseLayout)
    ConstraintLayout postExerciseLayout;

    @BindView(R.id.profileImageView)
    ImageView profileImageView;

    @BindView(R.id.userNameTextView)
    TextView userNameTextView;

    @BindView(R.id.dateTextView)
    TextView dateTextView;

    @BindView(R.id.exerciseOneImageView)
    ImageView exerciseOneImageView;

    @BindView(R.id.exerciseTwoImageView)
    ImageView exerciseTwoImageView;

    @BindView(R.id.exerciseNameTextView)
    TextView exerciseNameTextView;

    @BindView(R.id.likeButton)
    ImageView likeButton;

    @BindView(R.id.commentButton)
    ImageView commentButton;

    @BindView(R.id.commentCountTextView)
    TextView commentCountTextView;

    @BindView(R.id.likeCountTextView)
    TextView likeCountTextView;

    @BindView(R.id.noCommentsTextView)
    TextView noCommentsTextView;

    @BindView(R.id.loadingProgressbar)
    ProgressBar loadingProgressbar;

    @BindView(R.id.likeImage)
    ImageView likeImage;

    @BindView(R.id.targetMuscleTextView)
    TextView targetMuscleTextView;

    @BindView(R.id.commentEditText)
    EditText commentEditText;

    @BindView(R.id.recycler_view)
    RecyclerView commentsRecyclerView;

    @BindView(R.id.sendImageView)
    ImageView sendImageView;

    public static final String POST = "post";
    private PostCommentsViewModel mViewModel;

    public static PostCommentsFragment newInstance(Post post) {
        PostCommentsFragment postCommentsFragment = new PostCommentsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(POST, post);
        postCommentsFragment.setArguments(bundle);
        return postCommentsFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post_comments_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(PostCommentsViewModel.class);
        initRecycler();

        Post post = getArguments().getParcelable(POST);
        mViewModel.setPost(post);
        setDetails(post);


        likeButton.setOnClickListener(v -> {
            mViewModel.likePost();
        });


        //softkeyboard send action on keyboard does same job as send button
        commentEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                mViewModel.saveComment(commentEditText.getText().toString());
                commentEditText.setText("");
            }
            return false;
        });

    }

    private void initRecycler() {

        postCommentsAdapter = new PostCommentsAdapter(getActivity().getApplication(), this);
        commentsRecyclerView.setAdapter(postCommentsAdapter);
    }

    public void setDetails(final Post post) {
        Exercise exercise = post.getExercise();

        Glide.with(requireContext()).load(exercise.getCreatorImageUrl())
                .apply(new RequestOptions().placeholder(R.drawable.loading_animation).error(R.drawable.anonymous_profile))
                .circleCrop()
                .into(profileImageView);

        userNameTextView.setText(exercise.getCreatorName());

        dateTextView.setText(new PrettyTime().format(new Date((Long.parseLong(exercise.getDate())))));

        Glide.with(requireContext()).load(exercise.getPreviewPhotoOneUrl())
                .apply(new RequestOptions().placeholder(R.drawable.loading_animation).error(R.drawable.ic_exercise_grey))
                .into(exerciseOneImageView);

        Glide.with(requireContext()).load(exercise.getPreviewPhotoTwoUrl())
                .apply(new RequestOptions().placeholder(R.drawable.loading_animation).error(R.drawable.ic_exercise_2_grey))
                .into(exerciseTwoImageView);

        exerciseNameTextView.setText(exercise.getName());

        targetMuscleTextView.setText(exercise.getBodyPart());

        if (exercise.getLikeCount() != null)
            likeCountTextView.setText(Long.toString(exercise.getLikeCount()));


        likeButton.setOnClickListener(v -> {
            Log.d(TAG, "setDetails: ");
            // postCallback.onPostLike(exercise.getId(),getAdapterPosition());
        });

        commentButton.setOnClickListener(v -> {
            commentEditText.requestFocus();
        });

        sendImageView.setOnClickListener(v -> {
            if (commentEditText.getText().toString().trim().isEmpty()) {
                commentEditText.startAnimation(shakeError());
            } else {
                mViewModel.saveComment(commentEditText.getText().toString());
                commentEditText.setText("");
            }
        });


        mViewModel.getUserCommentPairListLiveData().observe(getViewLifecycleOwner(), userCommentPairList -> {
            if (userCommentPairList == null) {
                //post has no comments
                commentCountTextView.setText(0);
                noCommentsTextView.setVisibility(View.VISIBLE);
                commentsRecyclerView.setVisibility(View.GONE);
                loadingProgressbar.setVisibility(View.GONE);
            } else {
                //post has comments
                commentCountTextView.setText(String.valueOf(userCommentPairList.size()));
                noCommentsTextView.setVisibility(View.GONE);
                loadingProgressbar.setVisibility(View.GONE);
                commentsRecyclerView.setVisibility(View.VISIBLE);
                postCommentsAdapter.setData(userCommentPairList);
                commentsRecyclerView.scrollToPosition(userCommentPairList.size() - 1);
            }
        });


        mViewModel.getLikerIdListLiveData().observe(getViewLifecycleOwner(), likerIdList -> {
            if (likerIdList == null) {
                //post has NO likes
                likeCountTextView.setText("0");
                likeImage.setImageResource(R.drawable.ic_like_grey);
            } else {
                //post has likes
                likeCountTextView.setText(String.valueOf(likerIdList.size()));
                if (likerIdList.contains(AuthUtils.getLoggedUserId(getActivity().getApplication())))
                    likeImage.setImageResource(R.drawable.ic_like_blue);
                else
                    likeImage.setImageResource(R.drawable.ic_like_grey);
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