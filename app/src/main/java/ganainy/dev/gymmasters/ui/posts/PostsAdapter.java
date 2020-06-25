package ganainy.dev.gymmasters.ui.posts;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.Post;
import ganainy.dev.gymmasters.models.app_models.Workout;

import com.bumptech.glide.request.RequestOptions;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ganainy.dev.gymmasters.utils.AuthUtils;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "SharedAdapter";
    private static final int TYPE_WORKOUT = 0;
    private static final int TYPE_EXERCISE = 1;
    private final Application app;
    private List<Post> postList;
    private PostCallback postCallback;

    public PostsAdapter(Application app, PostCallback postCallback) {
        this.app = app;
        this.postCallback = postCallback;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view;
        if (viewType == TYPE_EXERCISE) {

            view = LayoutInflater.from(app).inflate(R.layout.post_exercise_item2, viewGroup, false);
            return new PostExerciseViewHolder(view);

        } else {
            view = LayoutInflater.from(app).inflate(R.layout.post_workout_item2, viewGroup, false);
            return new PostWorkoutViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_EXERCISE) {
            ((PostExerciseViewHolder) viewHolder).setDetails(postList.get(position));
        } else {
            ((PostWorkoutViewHolder) viewHolder).setDetails(postList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return postList == null ? 0 : postList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (postList.get(position).getEntityType() == 0) {
            return TYPE_EXERCISE;
        } else {
            return TYPE_WORKOUT;
        }

    }

    @Override
    public long getItemId(int position) {
        return postList.get(position).getId();
    }

    public void setData(List<Post> postList) {
        this.postList = postList;
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

        public void setDetails(final Post post) {
            Exercise exercise = post.getExercise();

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

            if (post.getExercise().getCommentList()!=null)
                commentCountTextView.setText(Long.toString(exercise.getCommentList().size()));
            else
                commentCountTextView.setText("0") ;

            if (post.getExercise().getLikerIdList()!=null)
            likeCountTextView.setText(Long.toString(exercise.getLikerIdList().size()));
            else
                likeCountTextView.setText("0") ;

            if (post.getExercise().getLikerIdList()!=null &&
                    post.getExercise().getLikerIdList().contains(AuthUtils.getLoggedUserId(app)))
                likeImage.setImageResource(R.drawable.ic_like_blue);
            else
                likeImage.setImageResource(R.drawable.ic_like_grey);


            likeButton.setOnClickListener(v -> {
                postCallback.onPostLike(post, getAdapterPosition());
            });

            commentButton.setOnClickListener(v -> {
                postCallback.onPostComment(post, 0);
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

        public void setDetails(Post post) {

            Workout workout = post.getWorkout();

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

            if (post.getWorkout().getCommentList()!=null)
                commentCountTextView.setText(Long.toString(workout.getCommentList().size()));
            else
                commentCountTextView.setText("0") ;

            if (post.getWorkout().getLikerIdList()!=null)
                likeCountTextView.setText(Long.toString(workout.getLikerIdList().size()));
            else
                likeCountTextView.setText("0") ;


            if (post.getWorkout().getLikerIdList()!=null &&
                    post.getWorkout().getLikerIdList().contains(AuthUtils.getLoggedUserId(app)))
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
                postCallback.onPostLike(post, getAdapterPosition());
            });

            commentButton.setOnClickListener(v -> {
                postCallback.onPostComment(post, 1);
            });

        }
    }
}
