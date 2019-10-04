package com.example.myapplication.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.Exercise;
import com.example.myapplication.ui.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExerciseAdapterAdvanced extends RecyclerView.Adapter<ExerciseAdapterAdvanced.ExerciseViewHolder> implements Filterable {
    private static final String TAG = "ExerciseAdapterAdvanced";
    private final Context context;

    private List<Exercise> exercisesList;
    private List<Exercise> ExercisesOfWorkoutList = new ArrayList<>();
    private StorageReference storageRef;
    private String sets, reps;

    public ExerciseAdapterAdvanced(Context context) {
        this.context = context;
        sets = "1";
        reps = "1";
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
        exerciseViewHolder.exerciseName.setText(exercisesList.get(i).getName());
        storageRef = FirebaseStorage.getInstance().getReference();
        //reference to exercise image
        downloadAndShowExerciseImage(exerciseViewHolder, i);


    }

    private void downloadAndShowExerciseImage(final ExerciseViewHolder exerciseViewHolder, int i) {
        StorageReference pathReference = storageRef.child("exerciseImages/" + exercisesList.get(i).getPreviewPhoto1());
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //download image with glide then show it in the navigation menu
                Glide.with(context).load(uri.toString()).into(exerciseViewHolder.exerciseImage);
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
        return exercisesList.size();
    }

    public void setDataSource(List<Exercise> exerciseList) {
        this.exercisesList = exerciseList;
    }


    @Override
    public Filter getFilter() {
        return null; /*new Filter() {
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
        };*/
    }

    private void openSetsAndRepsAlertDialog(final int adapterPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        MainActivity mainActivity = (MainActivity) context;
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.sets_reps_layout, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the
        // dialog layout
        builder.setTitle("Choose sets and repetitions");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_muscle);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addToExercisesOfWorkoutList(adapterPosition);

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });
        builder.create();
        builder.show();


        //two number pickers in alert dialog to choose sets and reps for clicked exercise
        NumberPicker setsPicker = view.findViewById(R.id.setsPicker);
        NumberPicker repsPicker = view.findViewById(R.id.repsPicker);

        setsPicker.setMinValue(1);
        setsPicker.setMaxValue(12);

        repsPicker.setMinValue(1);
        repsPicker.setMaxValue(100);


        setsPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                sets = String.valueOf(numberPicker.getValue());
            }
        });

        repsPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                reps = String.valueOf(numberPicker.getValue());
            }
        });
    }

    private void addToExercisesOfWorkoutList(int adapterPosition) {
        exercisesList.get(adapterPosition).setSets(sets);
        exercisesList.get(adapterPosition).setReps(reps);
        ExercisesOfWorkoutList.add(exercisesList.get(adapterPosition));
        Log.i(TAG, "addToExercisesOfWorkoutList: " + ExercisesOfWorkoutList.size());
    }


    public List<Exercise> getExercisesOfWorkoutList() {
        return ExercisesOfWorkoutList;
    }

    //viewHolder
    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        CircleImageView exerciseImage;
        TextView exerciseName;
        ImageView imageViewPlus;


        ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseImage = itemView.findViewById(R.id.exerciseImageView);
            exerciseName = itemView.findViewById(R.id.exerciseNameEdittext);
            imageViewPlus = itemView.findViewById(R.id.imageViewPlus);

            //open full exercise info when exercise from recycler is clicked
            imageViewPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSetsAndRepsAlertDialog(getAdapterPosition());
                }
            });
        }

    }


    //


}
