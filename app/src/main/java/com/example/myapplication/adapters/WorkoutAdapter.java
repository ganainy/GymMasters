package com.example.myapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.Workout;
import com.example.myapplication.ui.MainActivity;
import com.example.myapplication.ui.SpecificWorkoutActivity;
import com.example.myapplication.ui.UserInfoActivity;
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
        //download photo with glide then show it
        downloadWorkoutImage(workoutList.get(i).getPhotoLink(), workoutViewHolder);

    }

    private void downloadWorkoutImage(String photoLink, final WorkoutViewHolder workoutViewHolder) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(photoLink);

        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
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
        TextView textViewWorkoutName, textViewNumberOfExercises, textViewTime;
        ImageView workoutImageView;


        WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewWorkoutName = itemView.findViewById(R.id.textViewWorkoutName);
            textViewNumberOfExercises = itemView.findViewById(R.id.textViewNumberOfExercises);
            textViewTime = itemView.findViewById(R.id.textViewReps);
            workoutImageView = itemView.findViewById(R.id.workoutImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (parent.equals("userInfo")) {
                        UserInfoActivity userInfoActivity = (UserInfoActivity) context;
                        Intent intent = new Intent(WorkoutAdapter.this.context, SpecificWorkoutActivity.class);
                        intent.putExtra("workout", workoutList.get(getAdapterPosition()));
                        userInfoActivity.startActivity(intent);

                    } else if (parent.equals("fragmentWorkouts") | parent.equals("fragmentHome")) {
                        MainActivity mainActivity = (MainActivity) context;
                        Intent intent = new Intent(WorkoutAdapter.this.context, SpecificWorkoutActivity.class);
                        intent.putExtra("workout", workoutList.get(getAdapterPosition()));
                        mainActivity.startActivity(intent);
                    }

                }
            });
        }

    }


}
