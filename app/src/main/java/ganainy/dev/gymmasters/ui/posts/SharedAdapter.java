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

public class SharedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "SharedAdapter";
    private static final int TYPE_WORKOUT = 0;
    private static final int TYPE_EXERCISE = 1;
    private final Application app;
    private List<Post> postList;
    private PostCallback postCallback;

    public SharedAdapter(Application app, PostCallback postCallback) {
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
            view = LayoutInflater.from(app).inflate(R.layout.post_workout_item, viewGroup, false);
            return new PostWorkoutViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_EXERCISE) {
            ((PostExerciseViewHolder) viewHolder).setDetails(postList.get(position));
        } else {
            ((PostWorkoutViewHolder) viewHolder).setDetails(postList.get(position).getWorkout());
        }
    }

    @Override
    public int getItemCount() {
        return postList==null?0:postList.size();
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

    private String formatDate(String mDate) {
        Date date = new Date(Long.parseLong(mDate));
        return DateFormat.getDateInstance().format(date);
    }

    public void setData(List<Post> postList) {
        this.postList=postList;
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

            //todo open specific exercise activity
           /* itemView.setOnClickListener(view ->
                   postCallback.onExerciseClicked(currentExercise,getAdapterPosition()));*/
        }

        public void setDetails(final Post post) {
            Exercise exercise=post.getExercise();

            Glide.with(app).load(exercise.getCreatorImageUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.loading_animation).error(R.drawable.anonymous_profile))
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

            if (exercise.getLikeCount()!=null)
            likeCountTextView.setText(Long.toString(exercise.getLikeCount()));


                if (post.getLiked())
                    likeImage.setImageResource(R.drawable.ic_like_blue);
                else
                    likeImage.setImageResource(R.drawable.ic_like_grey);


          likeButton.setOnClickListener(v->{
              postCallback.onPostLike(exercise.getId(),getAdapterPosition());
          });

            commentButton.setOnClickListener(v->{
                postCallback.onPostComment(post,getAdapterPosition());
            });

        }
    }

    class PostWorkoutViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textViewWorkoutName)
        TextView textViewWorkoutName;
        @BindView(R.id.textView)
        TextView textView;
        @BindView(R.id.workoutImageView)
        ImageView workoutImageView;
        @BindView(R.id.textViewNumberOfExercises)
        TextView textViewNumberOfExercises;
        @BindView(R.id.textViewReps)
        TextView textViewReps;
        @BindView(R.id.targetMuscleTextView)
        TextView textViewDifficulty;

        Workout currentWorkout;

        public PostWorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            //open specific workout activity
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   /* Intent i = new Intent(app, SpecificWorkoutActivity.class);
                    i.putExtra("workout", currentWorkout);
                    PostsActivity postsActivity = (PostsActivity) app;
                    postsActivity.startActivity(i);*/
                }
            });

        }

        public void setDetails(final Workout workout) {
            currentWorkout = workout;
            textViewDifficulty.setText(workout.getLevel());
            //change bg color of difficultyTextView
            if (currentWorkout.getLevel().toLowerCase().equals("beginner")) {
                textViewDifficulty.setBackgroundResource(R.drawable.easy_bg);
            } else if (currentWorkout.getLevel().toLowerCase().equals("intermediate")) {
                textViewDifficulty.setBackgroundResource(R.drawable.intermediate_bg);
            } else if (currentWorkout.getLevel().toLowerCase().equals("professional")) {
                textViewDifficulty.setBackgroundResource(R.drawable.difficult_bg);
            }


            textViewWorkoutName.setText(workout.getName());
            textViewNumberOfExercises.setText(workout.getExercisesNumber());
            textViewReps.setText(workout.getDuration());
            //


        }
    }
}
