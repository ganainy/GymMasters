package ganainy.dev.gymmasters.shared_adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.ui.specificExercise.SpecificExerciseActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public  class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ExerciseViewHolder>  implements Filterable {
    private String parentName = "";
    List<Exercise> exercisesList;
    List<Exercise> filteredNameList;
    List<Exercise> originalExerciseList;
    ExerciseCallback exerciseCallback;
    Context context;
    private static final String TAG = "ExerciseAdapter";

    public ExercisesAdapter(Context context,ExerciseCallback exerciseCallback) {
        this.context = context;
        this.originalExerciseList = exercisesList;
        this.exerciseCallback=exerciseCallback;
    }

    public void setData(List<Exercise> exercisesList){
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
    public void onBindViewHolder(@NonNull ExerciseViewHolder exerciseViewHolder, int position) {
        exerciseViewHolder.exerciseName.setText(exercisesList.get(position).getName());

        if (parentName == null) {
            exerciseViewHolder.exerciseImage.setImageBitmap(exercisesList.get(position).getPreviewBitmap());
        } else {

            downloadAndShowExerciseImage(exerciseViewHolder, position);
        }
    }


    private void downloadAndShowExerciseImage(final ExerciseViewHolder exerciseViewHolder, int i) {

        StorageReference pathReference = FirebaseStorage.getInstance().getReference().child("exerciseImages/" + exercisesList.get(i).getPreviewPhoto1());
        pathReference.getDownloadUrl().addOnSuccessListener(uri ->
                Glide.with(context).load(uri.toString()).into(exerciseViewHolder.exerciseImage))
                .addOnFailureListener(exception -> {
                    // Handle any errors
                    Log.i(TAG, "onFailure: " + exception.getMessage());
                });
    }


    @Override
    public int getItemCount() {
        return exercisesList==null?0:exercisesList.size();
    }

    public void setDataSource(List<Exercise> exerciseList) {
        this.exercisesList = exerciseList;
    }



    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.i(TAG, "performFiltering: ");
                String charSequenceString = constraint.toString();
                if (charSequenceString.isEmpty()) {
                    filteredNameList = originalExerciseList;
                } else {
                    List<Exercise> filteredList = new ArrayList<>();
                    for (Exercise exercise : originalExerciseList) {
                        if (exercise.getName().toLowerCase().contains(charSequenceString.toLowerCase())) {
                            filteredList.add(exercise);
                        }
                        filteredNameList = filteredList;
                    }
                }
                FilterResults results = new FilterResults();
                Log.i(TAG, "performFiltering: filteredNameList" + filteredNameList.size());
                results.values = filteredNameList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                setDataSource((List<Exercise>) results.values);
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

            itemView.setOnClickListener(v -> exerciseCallback.onExerciseClick(exercisesList.get(getAdapterPosition())));
        }

    }


}

