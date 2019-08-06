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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.activities.ExercisesActivity;
import com.example.myapplication.activities.SpecificExerciseActivity;
import com.example.myapplication.model.Exercise;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public  class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>  implements Filterable {
    List<Exercise> exercisesList;
    List<Exercise> filteredNameList;
    Context context;
    private static final String TAG = "ExerciseAdapter";
    private StorageReference storageRef;

    public ExerciseAdapter(Context context,List<Exercise>exercisesList) {
        this.context = context;
        this.exercisesList=exercisesList;
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
        exerciseViewHolder.exerciseImage.setImageBitmap(exercisesList.get(i).getPreviewBitmap());
        Log.i(TAG, "onBindViewHolder: "+exercisesList.get(i).getPreviewBitmap());

      //  downloadAndShowExerciseImage(exercisesList.get(i).getPreviewPhoto1(), exerciseViewHolder);
    }

    private void downloadAndShowExerciseImage(String previewPhoto1, final ExerciseViewHolder exerciseViewHolder) {

        storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference pathReference = storageRef.child(previewPhoto1);
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

               // Glide.with((context)).load(uri.toString()).into(exerciseViewHolder.exerciseImage);
                Log.i(TAG, "onSuccess: loaded from storage");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.i(TAG, "onFailure: " + exception.getMessage());
            }
        });

    }



    @Override
    public int getItemCount() {
        return exercisesList.size() != 0 ? exercisesList.size() : 0;
    }

//    public void setDataSource(List<Exercise> exerciseList) {
//        this.exercisesList = exerciseList;
//    }



    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.i(TAG, "performFiltering: ");
                String charSequenceString = constraint.toString();
                if (charSequenceString.isEmpty()) {
                    filteredNameList = exercisesList;
                } else {
                    List<Exercise> filteredList = new ArrayList<>();
                    for (Exercise exercise : exercisesList) {
                        if (exercise.getName().toLowerCase().contains(charSequenceString.toLowerCase())) {
                            filteredList.add(exercise);
                        }
                        filteredNameList = filteredList;
                    }

                }
                FilterResults results = new FilterResults();
                results.values = filteredNameList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                Log.i(TAG, "publishResults,exercisesList: "+exercisesList.size());
                exercisesList = (List<Exercise>) results.values;
                notifyDataSetChanged();
            }
        };
    }

            //viewHolder
            class ExerciseViewHolder extends RecyclerView.ViewHolder {
                CircleImageView exerciseImage;
                TextView exerciseName;


                ExerciseViewHolder(@NonNull View itemView) {
                    super(itemView);
                    exerciseImage = itemView.findViewById(R.id.exerciseImageView);
                    exerciseName = itemView.findViewById(R.id.exerciseNameEdittext);

                    //open full exercise info when exercise from recycler is clicked
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ExercisesActivity exercisesActivity = (ExercisesActivity) context;
                            exercisesActivity.handleClick(exercisesList.get(getAdapterPosition()));
                        }
                    });
                }

            }




}
