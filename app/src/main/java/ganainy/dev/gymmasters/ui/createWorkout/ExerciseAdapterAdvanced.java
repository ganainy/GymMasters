package ganainy.dev.gymmasters.ui.createWorkout;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.Exercise;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExerciseAdapterAdvanced extends RecyclerView.Adapter<ExerciseAdapterAdvanced.ExerciseViewHolder>  {
    private static final String TAG = "ExerciseAdapterAdvanced";
    public static final String EXERCISE_IMAGES = "exerciseImages/";

    private final Context context;
    private List<Exercise> exerciseList;
    private AddExerciseCallback addExerciseCallback;

    public ExerciseAdapterAdvanced(Context context, AddExerciseCallback addExerciseCallback) {
        this.context = context;
        this.addExerciseCallback = addExerciseCallback;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.exercise_item_advanced,
                viewGroup, false);
        return new ExerciseAdapterAdvanced.ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder exerciseViewHolder, int i) {

        if (exerciseList.get(i).getIsAddedToWorkout() != null && exerciseList.get(i).getIsAddedToWorkout()) {
            //exercise is added to workout
            exerciseViewHolder.parentConstraint.setBackgroundResource(R.drawable.circular_green_bordersolid);
            exerciseViewHolder.AlreadyAddedLayout.setVisibility(View.VISIBLE);
            exerciseViewHolder.imageViewPlus.setVisibility(View.GONE);
            if (exerciseList.get(i).getReps()!=null) {
                exerciseViewHolder.repsCountTextView.setVisibility(View.VISIBLE);
                exerciseViewHolder.repsCountTextView.setText(context.getString(R.string.reps,exerciseList.get(i).getReps()));
            }
            if (exerciseList.get(i).getSets()!=null) {
                exerciseViewHolder.setsCountTextView.setVisibility(View.VISIBLE);
                exerciseViewHolder.setsCountTextView.setText(context.getString(R.string.sets,exerciseList.get(i).getSets()));
            }
        } else {
            exerciseViewHolder.parentConstraint.setBackgroundResource(R.drawable.circular_grey_bordersolid);
            exerciseViewHolder.AlreadyAddedLayout.setVisibility(View.GONE);
            exerciseViewHolder.imageViewPlus.setVisibility(View.VISIBLE);
            exerciseViewHolder.setsCountTextView.setVisibility(View.GONE);
            exerciseViewHolder.repsCountTextView.setVisibility(View.GONE);
        }

        exerciseViewHolder.exerciseName.setText(exerciseList.get(i).getName());
        //reference to exercise image
        downloadAndShowExerciseImage(exerciseViewHolder, i);
    }

    private void downloadAndShowExerciseImage(final ExerciseViewHolder exerciseViewHolder, int i) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference pathReference = storageRef.child(EXERCISE_IMAGES + exerciseList.get(i).getPreviewPhotoOneUrl());
        pathReference.getDownloadUrl().addOnSuccessListener(uri ->
                Glide.with(context).load(uri.toString()).into(exerciseViewHolder.exerciseImage))
                .addOnFailureListener(exception
                        -> {
                    Log.i(TAG, "onFailure: " + exception.getMessage());
                });
    }

    @Override
    public int getItemCount() {
        return exerciseList == null ? 0 : exerciseList.size();
    }

    public void setDataSource(List<Exercise> exerciseList) {
        this.exerciseList = exerciseList;
    }

    //viewHolder
    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        CircleImageView exerciseImage;
        TextView exerciseName,repsCountTextView,setsCountTextView;
        ImageView imageViewPlus, deleteImageView;
        ConstraintLayout parentConstraint;
        LinearLayout AlreadyAddedLayout;


        ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseImage = itemView.findViewById(R.id.workoutImageView);
            deleteImageView = itemView.findViewById(R.id.deleteImageView);
            exerciseName = itemView.findViewById(R.id.exerciseNameEdittext);
            imageViewPlus = itemView.findViewById(R.id.imageViewDelete);
            parentConstraint = itemView.findViewById(R.id.parentConstraint);
            AlreadyAddedLayout = itemView.findViewById(R.id.AlreadyaddedLayout);
            repsCountTextView = itemView.findViewById(R.id.repsCountTextView);
            setsCountTextView = itemView.findViewById(R.id.setsCountTextView);

            /*delegate interactions to parent*/
            imageViewPlus.setOnClickListener(v -> {
                        addExerciseCallback.onExercisesAdded(exerciseList.get(getAdapterPosition()),getAdapterPosition());
                    }
            );

            deleteImageView.setOnClickListener(view -> addExerciseCallback.onExercisesDeleted(exerciseList.get(getAdapterPosition()),getAdapterPosition()));
        }

    }

}
