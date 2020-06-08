package ganainy.dev.gymmasters.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import ganainy.dev.gymmasters.model.Exercise;
import ganainy.dev.gymmasters.ui.ExercisesActivity;
import ganainy.dev.gymmasters.ui.SpecificExerciseActivity;
import ganainy.dev.gymmasters.ui.UserInfoActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public  class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>  implements Filterable {
    private String parentName = "";
    List<Exercise> exercisesList;
    List<Exercise> filteredNameList;
    List<Exercise> originalExerciseList;
    Context context;
    private static final String TAG = "ExerciseAdapter";
    private boolean isParentDead;

    public ExerciseAdapter(Context context,List<Exercise>exercisesList) {
        this.context = context;
        this.exercisesList=exercisesList;
        this.originalExerciseList = exercisesList;
    }

    public ExerciseAdapter(Context context, List<Exercise> exercisesList, String parentName) {
        this.parentName = parentName;
        this.context = context;
        this.exercisesList = exercisesList;
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

        if (parentName == null) {
            exerciseViewHolder.exerciseImage.setImageBitmap(exercisesList.get(i).getPreviewBitmap());
        } else {

            downloadAndShowExerciseImage(exerciseViewHolder, i);
        }

    }


    private void downloadAndShowExerciseImage(final ExerciseAdapter.ExerciseViewHolder exerciseViewHolder, int i) {

        StorageReference pathReference = FirebaseStorage.getInstance().getReference().child("exerciseImages/" + exercisesList.get(i).getPreviewPhoto1());
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (!isParentDead) {
                    Log.i(TAG, "shouldnt call after detatch");
                    Glide.with(context).load(uri.toString()).into(exerciseViewHolder.exerciseImage);
                }
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
                            //Different intents depending on the fragment/activity which called this adapter
                            if (parentName.equals("home")) {
                                //adapter called by main fragment home
                                Intent intent = new Intent(context, SpecificExerciseActivity.class);
                                //parcelable have size limit so i wont pass image bitmap with the exercise
                                Exercise exercise = exercisesList.get(getAdapterPosition());
                                intent.putExtra("exercise", exercise);
                                intent.putExtra("ownExercise", true);
                                context.startActivity(intent);
                            } else if (parentName.equals("userInfo")) {
                                UserInfoActivity userInfoActivity = (UserInfoActivity) context;
                                Intent intent = new Intent(context, SpecificExerciseActivity.class);
                                //parcelable have size limit so i wont pass image bitmap with the exercise
                                Exercise exercise = exercisesList.get(getAdapterPosition());
                                intent.putExtra("exercise", exercise);
                                context.startActivity(intent);

                            } else {
                                //adapter called by specficmuscle fragment which on exercise activity
                                ExercisesActivity exercisesActivity = (ExercisesActivity) context;
                                exercisesActivity.handleClick(exercisesList.get(getAdapterPosition()));
                            }

                        }
                    });
                }

            }


    @Override
    public void onViewDetachedFromWindow(@NonNull ExerciseViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        Log.i(TAG, "onViewDetachedFromWindow: ");
        isParentDead = true;

    }

    @Override
    public void onViewAttachedToWindow(@NonNull ExerciseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        isParentDead = false;
    }
}
