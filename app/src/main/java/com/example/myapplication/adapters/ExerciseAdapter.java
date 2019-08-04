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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.activities.ExercisesActivity;
import com.example.myapplication.activities.SpecificExerciseActivity;
import com.example.myapplication.model.Exercise;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public  class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {
    List<Exercise> exercisesList;
    Context context;
    private static final String TAG = "ExerciseAdapter";
    private StorageReference storageRef;

    public ExerciseAdapter(Context context) {
        this.context=context;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.excercise_item,
                viewGroup, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder exerciseViewHolder, int i) {
        exerciseViewHolder.exerciseName.setText(exercisesList.get(i).getName());
      downloadAndShowExerciseImage(exercisesList.get(i).getPreviewPhoto1(),exerciseViewHolder);
    }

    private void downloadAndShowExerciseImage(String previewPhoto1, final ExerciseViewHolder exerciseViewHolder) {

        storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference pathReference = storageRef.child(previewPhoto1);
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with((context)).load(uri.toString()).into(exerciseViewHolder.exerciseImage);
                Log.i(TAG, "onSuccess: loaded from storage");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.i(TAG, "onFailure: "+exception.getMessage());
            }
        });

        }


    @Override
    public int getItemCount() {
        return exercisesList.size()!=0?exercisesList.size():0;
    }

    public void  setDataSource(List<Exercise> exerciseList)
    {
        this.exercisesList = exerciseList;
    }

    //viewHolder
    public class ExerciseViewHolder extends RecyclerView.ViewHolder{
        CircleImageView exerciseImage;
    TextView exerciseName;


        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseImage=itemView.findViewById(R.id.exerciseImageView);
            exerciseName=itemView.findViewById(R.id.exerciseNameEdittext);

            //open full exercise info when exercise from recycler is clicked
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   ExercisesActivity exercisesActivity= (ExercisesActivity)context;
                    exercisesActivity.handleClick(exercisesList.get(getAdapterPosition()));
                }
            });
        }

    }
}
