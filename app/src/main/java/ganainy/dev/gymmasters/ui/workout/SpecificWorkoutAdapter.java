package ganainy.dev.gymmasters.ui.workout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.Exercise;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpecificWorkoutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "SpecificWorkoutAdapter";
    private static final int TYPE_REPS = 1;
    private static final int TYPE_TIME = 2;
    private static final int TYPE_DOT = 3;
    private final Context context;
    private List<Exercise> workoutExerciseList;
    private ExerciseInsideWorkoutCallback exerciseInsideWorkoutCallback;

    public SpecificWorkoutAdapter(Context context, ExerciseInsideWorkoutCallback exerciseInsideWorkoutCallback) {
        this.context = context;
        this.exerciseInsideWorkoutCallback=exerciseInsideWorkoutCallback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == TYPE_REPS) { // for TYPE_REPS layout
            view = LayoutInflater.from(context).inflate(R.layout.inside_workout_item_reps, viewGroup, false);
            return new RepsExerciseViewHolder(view);
        } else if (viewType==TYPE_TIME){ // for TYPE_TIME layout
            view = LayoutInflater.from(context).inflate(R.layout.inside_workout_item_duration, viewGroup, false);
            return new TimedExerciseViewHolder(view);
        }else if (viewType==TYPE_DOT){
            view = LayoutInflater.from(context).inflate(R.layout.dot_item, viewGroup, false);
            return new DotViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof RepsExerciseViewHolder){
            ((RepsExerciseViewHolder) viewHolder).setDetails(workoutExerciseList.get(position));
        }else if (viewHolder instanceof TimedExerciseViewHolder){
            ((TimedExerciseViewHolder) viewHolder).setDetails(workoutExerciseList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return workoutExerciseList==null?0:workoutExerciseList.size();
    }

    @Override
    public int getItemViewType(int position) {

        Exercise currentExercise = workoutExerciseList.get(position);

        if (currentExercise==null){
            return TYPE_DOT;
        }
        else if (currentExercise.getDuration() != null) {
            return TYPE_TIME;
        } else if (currentExercise.getSets() !=null && currentExercise.getReps()!=null){
            return TYPE_REPS;
        }

        return -1;
    }

    public void setData(List<Exercise> workoutExerciseList) {
        if (workoutExerciseList.get(0)!=null){
            workoutExerciseList.add(0,null);
        }
        if (workoutExerciseList.get(workoutExerciseList.size()-1)!=null){
            workoutExerciseList.add(null);
        }
        this.workoutExerciseList = workoutExerciseList;
    }


    class RepsExerciseViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textViewExName)
        TextView textViewExName;

        @BindView(R.id.textViewSets)
        TextView textViewSets;

        @BindView(R.id.textViewReps)
        TextView textViewReps;

        @BindView(R.id.textViewTargetMuscle)
        TextView textViewTargetMuscle;

        @BindView(R.id.exerciseImageView)
        ImageView exerciseImageView;


        RepsExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(view ->
                    exerciseInsideWorkoutCallback.onRepsExerciseClicked(workoutExerciseList.get(getAdapterPosition()),getAdapterPosition()));
        }

        public void setDetails(Exercise workoutExercise) {
            textViewExName.setText(workoutExercise.getName());
            textViewSets.setText(workoutExercise.getSets() + " Sets");
            textViewReps.setText(workoutExercise.getReps() + " Reps");
            textViewTargetMuscle.setText(workoutExercise.getBodyPart());

     /*       Glide.with(context).load(workoutExercise.getPreviewPhotoOneUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.loading_animation).error(R.drawable.ic_dumbell_grey))
                    .circleCrop()
                    .into(exerciseImageView);*/
        }
    }

    class TimedExerciseViewHolder extends RecyclerView.ViewHolder {
        //todo add load photo and allow user to create timed exercise
        @BindView(R.id.textViewExName)
        TextView textViewExName;

        @BindView(R.id.textViewSets)
        TextView textViewSets;

        @BindView(R.id.textViewTime)
        TextView textViewTime;

        @BindView(R.id.textViewTargetMuscle)
        TextView textViewTargetMuscle;

        TimedExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(view ->
                    exerciseInsideWorkoutCallback.onTimeExerciseClicked(workoutExerciseList.get(getAdapterPosition()),getAdapterPosition()));
        }

        public void setDetails(Exercise workoutExercise) {
            textViewExName.setText(workoutExercise.getName());
            textViewSets.setText(workoutExercise.getSets() + " Sets");
            textViewTime.setText(workoutExercise.getDuration() + " Secs");
            textViewTargetMuscle.setText(workoutExercise.getBodyPart());
        }
    }

    class DotViewHolder extends RecyclerView.ViewHolder {
        DotViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}
