package ganainy.dev.gymmasters.shared_adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.Workout;
import ganainy.dev.gymmasters.ui.main.MainActivity;
import ganainy.dev.gymmasters.ui.specificWorkout.SpecificWorkoutActivity;
import ganainy.dev.gymmasters.ui.userInfo.UserInfoActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    private static final String TAG = "WorkoutAdapter";
    final Context context;
    private final String parent;
    private List<Workout> workoutList;
    private boolean isParentDead;


    public WorkoutAdapter(Context context, String parent) {
        this.context = context;
        this.parent = parent;
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
        workoutViewHolder.textViewWorkoutName.setText(workoutList.get(i).getName());
        workoutViewHolder.textViewNumberOfExercises.setText(workoutList.get(i).getExercisesNumber());
        workoutViewHolder.textViewTime.setText(workoutList.get(i).getDuration());
        workoutViewHolder.difficultyTextView.setText(workoutList.get(i).getLevel());
        //change bg color of difficultyTextView
        if (workoutList.get(i).getLevel().toLowerCase().equals("beginner")) {
            workoutViewHolder.difficultyTextView.setBackgroundResource(R.drawable.easy_bg);
        } else if (workoutList.get(i).getLevel().toLowerCase().equals("intermediate")) {
            workoutViewHolder.difficultyTextView.setBackgroundResource(R.drawable.intermediate_bg);
        } else if (workoutList.get(i).getLevel().toLowerCase().equals("professional")) {
            workoutViewHolder.difficultyTextView.setBackgroundResource(R.drawable.difficult_bg);
        }
        //download photo with glide then show it
        downloadWorkoutImage(workoutList.get(i).getPhotoLink(), workoutViewHolder);

    }

    private void downloadWorkoutImage(String photoLink, final WorkoutViewHolder workoutViewHolder) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(photoLink);

        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (!isParentDead)
                    Glide.with(context).load(uri).into(workoutViewHolder.workoutImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "workout photo download failed " + e.getMessage());
            }
        });

    }


    @Override
    public int getItemCount() {
        return workoutList.size();
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
            difficultyTextView = itemView.findViewById(R.id.textViewDifficulty);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (parent.equals("userInfo")) {
                        UserInfoActivity userInfoActivity = (UserInfoActivity) context;
                        Intent intent = new Intent(WorkoutAdapter.this.context, SpecificWorkoutActivity.class);
                        intent.putExtra("workout", workoutList.get(getAdapterPosition()));
                        userInfoActivity.startActivity(intent);

                    } else if (parent.equals("fragmentWorkouts")) {
                        MainActivity mainActivity = (MainActivity) context;
                        Intent intent = new Intent(WorkoutAdapter.this.context, SpecificWorkoutActivity.class);
                        intent.putExtra("workout", workoutList.get(getAdapterPosition()));
                        mainActivity.startActivity(intent);
                    } else if (parent.equals("fragmentHome")) {
                        MainActivity mainActivity = (MainActivity) context;
                        Intent intent = new Intent(WorkoutAdapter.this.context, SpecificWorkoutActivity.class);
                        intent.putExtra("workout", workoutList.get(getAdapterPosition()));
                        intent.putExtra("ownWorkout", true);
                        mainActivity.startActivity(intent);
                    }

                }
            });
        }

    }


    /*to prevent glide from loading images if the parent layout already destroyed*/

    @Override
    public void onViewDetachedFromWindow(@NonNull WorkoutViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        isParentDead = true;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull WorkoutViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        isParentDead = false;
    }


}
