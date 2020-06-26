package ganainy.dev.gymmasters.shared_adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.Workout;
import ganainy.dev.gymmasters.ui.exercise.WorkoutCallback;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    private static final String TAG = "WorkoutAdapter";
    public static final String BEGINNER = "beginner";
    public static final String INTERMEDIATE = "intermediate";
    public static final String PROFESSIONAL = "professional";
    final Context app;
    private List<Workout> workoutList;
    private WorkoutCallback workoutCallback;


    public WorkoutAdapter(Context app, WorkoutCallback workoutCallback) {
        this.app = app;
        this.workoutCallback = workoutCallback;

    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.workout_item,
                viewGroup, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder workoutViewHolder, int i) {
        Workout currentWorkout = workoutList.get(i);
        workoutViewHolder.textViewWorkoutName.setText(currentWorkout.getName());
        workoutViewHolder.textViewNumberOfExercises.setText(currentWorkout.getExercisesNumber());
        workoutViewHolder.textViewTime.setText(currentWorkout.getDuration());
        workoutViewHolder.difficultyTextView.setText(currentWorkout.getLevel());
        //change bg color of difficultyTextView
        if (currentWorkout.getLevel().toLowerCase().equals(BEGINNER)) {
            workoutViewHolder.difficultyTextView.setBackgroundResource(R.drawable.easy_bg);
        } else if (currentWorkout.getLevel().toLowerCase().equals(INTERMEDIATE)) {
            workoutViewHolder.difficultyTextView.setBackgroundResource(R.drawable.intermediate_bg);
        } else if (currentWorkout.getLevel().toLowerCase().equals(PROFESSIONAL)) {
            workoutViewHolder.difficultyTextView.setBackgroundResource(R.drawable.difficult_bg);
        }

        //load photo and show shimmer effect as placeholder while loading
        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder()
                .setDuration(1800)
                .setBaseAlpha(0.7f)
                .setHighlightAlpha(0.6f)
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .setAutoStart(true)
                .build();

        // This is the placeholder for the imageView
        ShimmerDrawable shimmerDrawable =new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);

        Glide.with(app).load(currentWorkout.getPhotoLink())
                .apply(new RequestOptions().placeholder(shimmerDrawable).error(R.drawable.ic_exercise_grey))
                .into(workoutViewHolder.workoutImageView);
    }



    @Override
    public int getItemCount() {
        return workoutList==null?0:workoutList.size();
    }

    public void setDataSource(List<Workout> workoutList) {
        this.workoutList = workoutList;
    }

    //viewHolder
    class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView textViewWorkoutName, textViewNumberOfExercises, textViewTime, difficultyTextView;
        ImageView workoutImageView;


        WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewWorkoutName = itemView.findViewById(R.id.textViewWorkoutName);
            textViewNumberOfExercises = itemView.findViewById(R.id.textViewNumberOfExercises);
            textViewTime = itemView.findViewById(R.id.textViewReps);
            workoutImageView = itemView.findViewById(R.id.workoutImageView);
            difficultyTextView = itemView.findViewById(R.id.workoutDurationTextView);

            itemView.setOnClickListener(view -> workoutCallback.onWorkoutClicked(workoutList.get(getAdapterPosition())));
        }

    }

}
