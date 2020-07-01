package ganainy.dev.gymmasters.ui.main.posts.postComments;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.Comment;
import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.models.app_models.Workout;
import ganainy.dev.gymmasters.ui.main.posts.PostCallback;
import ganainy.dev.gymmasters.utils.AuthUtils;

/**
 * this adapter can be used to show exercises/workouts/post comments
 */
public class PostCommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int RECYCLER_TYPE_WORKOUT = 0;
    private static final int RECYCLER_TYPE_EXERCISE = 1;
    private static final int RECYCLER_TYPE_COMMENT = 2;
    private static final int RECYCLER_TYPE_NO_COMMENTS = 3;
    private static final int RECYCLER_TYPE_LOADING = 4;
    Application app;
    List<PostComment> postCommentList;
    PostCallback postCallback;

    public PostCommentsAdapter(Application app, PostCallback postCallback) {
        this.app = app;
        this.postCallback = postCallback;
    }

    public void setData(List<PostComment> postCommentList) {
        this.postCommentList = postCommentList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType== RECYCLER_TYPE_COMMENT){
            View view = LayoutInflater.from(app).inflate(R.layout.comment_item, viewGroup, false);
            return new PostCommentViewHolder(view);
        }else if (viewType== RECYCLER_TYPE_EXERCISE){
            View view = LayoutInflater.from(app).inflate(R.layout.post_exercise_item, viewGroup, false);
            return new PostExerciseViewHolder(view);
        }
        else if (viewType== RECYCLER_TYPE_WORKOUT){
            View view = LayoutInflater.from(app).inflate(R.layout.post_workout_item, viewGroup, false);
            return new PostWorkoutViewHolder(view);
        }else if (viewType== RECYCLER_TYPE_NO_COMMENTS){
            View view = LayoutInflater.from(app).inflate(R.layout.empty_comments_item, viewGroup, false);
            return new EmptyCommentsViewHolder(view);
        }else if (viewType== RECYCLER_TYPE_LOADING){
            View view = LayoutInflater.from(app).inflate(R.layout.loading_item, viewGroup, false);
            return new LoadingViewHolder(view);
        }
        return null;//error
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof PostExerciseViewHolder) {
            ((PostExerciseViewHolder) viewHolder).setDetails(postCommentList.get(position));
        } else if (viewHolder instanceof PostWorkoutViewHolder) {
            ((PostWorkoutViewHolder) viewHolder).setDetails(postCommentList.get(position));
        } else if (viewHolder instanceof PostCommentViewHolder) {
            ((PostCommentViewHolder) viewHolder).setDetails(postCommentList.get(position));
        }


    }

    @Override
    public int getItemCount() {
        return postCommentList == null ? 0 : postCommentList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (postCommentList.get(position).getPostCommentType().equals(PostComment.PostCommentType.POST_EXERCISE)) {
            return RECYCLER_TYPE_EXERCISE;
        } else if (postCommentList.get(position).getPostCommentType().equals(PostComment.PostCommentType.POST_WORKOUT)) {
            return RECYCLER_TYPE_WORKOUT;
        } else if (postCommentList.get(position).getPostCommentType().equals(PostComment.PostCommentType.COMMENT)) {
            return RECYCLER_TYPE_COMMENT;
        } else if (postCommentList.get(position).getPostCommentType().equals(PostComment.PostCommentType.LOADING_COMMENTS)) {
            return RECYCLER_TYPE_LOADING;
        }else if (postCommentList.get(position).getPostCommentType().equals(PostComment.PostCommentType.EMPTY_COMMENTS)) {
            return RECYCLER_TYPE_NO_COMMENTS;
        }

        return -1;

    }


    public class PostCommentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.commenterImageView)
        ImageView commenterImageView;

        @BindView(R.id.commenterNameTextView)
        TextView commenterNameTextView;

        @BindView(R.id.commentTextView)
        TextView commentTextView;

        @BindView(R.id.commentDateTextView)
        TextView commentDateTextView;


        public PostCommentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setDetails(PostComment postComment) {
            Pair<Comment, User> currentUserCommentPair = postComment.getUserCommentPair();
            Glide.with(app).load(currentUserCommentPair.second.getPhoto())
                    .apply(new RequestOptions().placeholder(R.drawable.loading_animation).error(R.drawable.anonymous_profile))
                    .circleCrop()
                    .into(commenterImageView);

            commenterNameTextView.setText(currentUserCommentPair.second.getName());

            commentTextView.setText(currentUserCommentPair.first.getText());

            commentDateTextView.setText(new PrettyTime().format(new Date(currentUserCommentPair.first.getDateCreated())));
        }
    }

    class PostExerciseViewHolder extends RecyclerView.ViewHolder {

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

        @BindView(R.id.likeImage)
        ImageView likeImage;

        @BindView(R.id.targetMuscleTextView)
        TextView targetMuscleTextView;

        public PostExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setDetails(final PostComment postComment) {
            Exercise exercise = postComment.getPost().getExercise();

            Glide.with(app).load(exercise.getCreatorImageUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.loading_animation).error(R.drawable.anonymous_profile))
                    .circleCrop()
                    .into(profileImageView);

            userNameTextView.setText(exercise.getCreatorName());

            dateTextView.setText(new PrettyTime().format(new Date((Long.parseLong(exercise.getDate())))));

            Glide.with(app).load(exercise.getPreviewPhotoOneUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.loading_animation).error(R.drawable.ic_exercise_grey))
                    .into(exerciseOneImageView);

            Glide.with(app).load(exercise.getPreviewPhotoTwoUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.loading_animation).error(R.drawable.ic_exercise_2_grey))
                    .into(exerciseTwoImageView);

            exerciseNameTextView.setText(exercise.getName());

            targetMuscleTextView.setText(exercise.getBodyPart());

            if (exercise.getCommentList() != null)
                commentCountTextView.setText(Long.toString(exercise.getCommentList().size()));
            else
                commentCountTextView.setText("0");

            if (exercise.getLikerIdList() != null)
                likeCountTextView.setText(Long.toString(exercise.getLikerIdList().size()));
            else
                likeCountTextView.setText("0");

            if (exercise.getLikerIdList() != null &&
                    exercise.getLikerIdList().contains(AuthUtils.getLoggedUserId(app)))
                likeImage.setImageResource(R.drawable.ic_like_blue);
            else
                likeImage.setImageResource(R.drawable.ic_like_grey);


            likeButton.setOnClickListener(v -> {
                postCallback.onPostLike(postComment.getPost(), getAdapterPosition());
            });

            commentButton.setOnClickListener(v -> {
                postCallback.onPostComment(postComment.getPost(), getAdapterPosition());
            });

        }
    }

    class PostWorkoutViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.profileImageView)
        ImageView profileImageView;

        @BindView(R.id.userNameTextView)
        TextView userNameTextView;

        @BindView(R.id.dateTextView)
        TextView dateTextView;

        @BindView(R.id.workoutImageView)
        ImageView workoutImageView;

        @BindView(R.id.workoutNameTextView)
        TextView workoutNameTextView;

        @BindView(R.id.likeButton)
        ImageView likeButton;

        @BindView(R.id.commentButton)
        ImageView commentButton;

        @BindView(R.id.commentCountTextView)
        TextView commentCountTextView;

        @BindView(R.id.likeCountTextView)
        TextView likeCountTextView;

        @BindView(R.id.likeImage)
        ImageView likeImage;

        @BindView(R.id.workoutDurationTextView)
        TextView workoutDurationTextView;

        @BindView(R.id.workoutDifficultyTextView)
        TextView workoutDifficultyTextView;

        @BindView(R.id.difficultyBackgroundImageView)
        ImageView difficultyBackgroundImageView;

        public PostWorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setDetails(PostComment postComment) {

            Workout workout = postComment.getPost().getWorkout();

            Glide.with(app).load(workout.getCreatorImageUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.loading_animation).error(R.drawable.anonymous_profile))
                    .circleCrop()
                    .into(profileImageView);

            userNameTextView.setText(workout.getCreatorName());

            workoutNameTextView.setText(workout.getName());

            dateTextView.setText(new PrettyTime().format(new Date((Long.parseLong(workout.getDate())))));

            Glide.with(app).load(workout.getPhotoLink())
                    .apply(new RequestOptions().placeholder(R.drawable.loading_animation).error(R.drawable.ic_exercise_grey))
                    .into(workoutImageView);

            workoutDurationTextView.setText(workout.getDuration());

            workoutDifficultyTextView.setText(workout.getLevel());

            if (workout.getCommentList() != null)
                commentCountTextView.setText(Long.toString(workout.getCommentList().size()));
            else
                commentCountTextView.setText("0");

            if (workout.getLikerIdList() != null)
                likeCountTextView.setText(Long.toString(workout.getLikerIdList().size()));
            else
                likeCountTextView.setText("0");


            if (workout.getLikerIdList() != null &&
                    workout.getLikerIdList().contains(AuthUtils.getLoggedUserId(app)))
                likeImage.setImageResource(R.drawable.ic_like_blue);
            else
                likeImage.setImageResource(R.drawable.ic_like_grey);


            switch (workout.getLevel().toLowerCase()) {
                case "beginner":
                    difficultyBackgroundImageView.setImageResource(R.drawable.ic_triangle_green);
                    workoutDifficultyTextView.setText(R.string.beginner);
                    break;
                case "intermediate":
                    difficultyBackgroundImageView.setImageResource(R.drawable.ic_triangle_yellow);
                    workoutDifficultyTextView.setText(R.string.intermediate);
                    break;
                case "professional":
                    difficultyBackgroundImageView.setImageResource(R.drawable.ic_triangle_red);
                    workoutDifficultyTextView.setText(R.string.professional);
                    break;
            }


            likeButton.setOnClickListener(v -> {
                postCallback.onPostLike(postComment.getPost(), getAdapterPosition());
            });

            commentButton.setOnClickListener(v -> {
                postCallback.onPostComment(postComment.getPost(), getAdapterPosition());
            });

        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class EmptyCommentsViewHolder extends RecyclerView.ViewHolder {

        public EmptyCommentsViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
