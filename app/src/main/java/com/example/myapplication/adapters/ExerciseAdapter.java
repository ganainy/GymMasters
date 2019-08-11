package com.example.myapplication.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.activities.ExercisesActivity;
import com.example.myapplication.model.Exercise;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public  class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>  implements Filterable {
    List<Exercise> exercisesList;
    List<Exercise> filteredNameList;
    List<Exercise> originalExerciseList;
    Context context;
    private static final String TAG = "ExerciseAdapter";

    public ExerciseAdapter(Context context,List<Exercise>exercisesList) {
        this.context = context;
        this.exercisesList=exercisesList;
        this.originalExerciseList = exercisesList;
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

    }




    @Override
    public int getItemCount() {
        return exercisesList.size();
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
