package ganainy.dev.gymmasters.shared_adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.Exercise;
import com.bumptech.glide.request.RequestOptions;
import java.util.List;



public  class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ExerciseViewHolder> {

    List<Exercise> exercisesList;
    ExerciseCallback exerciseCallback;
    Context context;

    private static final String TAG = "ExerciseAdapter";

    public ExercisesAdapter(Context context,ExerciseCallback exerciseCallback) {
        this.context = context;
        this.exerciseCallback=exerciseCallback;
    }

    public void setData(List<Exercise> exercisesList){
        this.exercisesList=exercisesList;
    }


    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.exercise_item,
                viewGroup, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder exerciseViewHolder, int position) {
        Exercise currentExercise = exercisesList.get(position);
        exerciseViewHolder.exerciseName.setText(currentExercise.getName());

        Glide.with(context).load(currentExercise.getPreviewPhotoOneUrl())
                .apply(new RequestOptions().placeholder(R.drawable.loading_animation).error(R.drawable.ic_dumbell_grey))
                .into(exerciseViewHolder.exerciseImage);
    }

    @Override
    public int getItemCount() {
        return exercisesList==null?0:exercisesList.size();
    }

    //viewHolder
    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        ImageView exerciseImage;
        TextView exerciseName;


        ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseImage = itemView.findViewById(R.id.workoutImageView);
            exerciseName = itemView.findViewById(R.id.exerciseNameEdittext);

            itemView.setOnClickListener(v -> exerciseCallback.onExerciseClick(exercisesList.get(getAdapterPosition())));
        }

    }


}

