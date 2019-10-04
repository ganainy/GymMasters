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
import com.example.myapplication.model.Exercise;
import com.example.myapplication.model.Workout;
import com.example.myapplication.ui.PostsActivity;
import com.example.myapplication.ui.SpecificExerciseActivity;
import com.example.myapplication.ui.SpecificWorkoutActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SharedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "SharedAdapter";
    private static final int TYPE_WORKOUT = 0;
    private static final int TYPE_EXERCISE = 1;
    private final Context context;
    private final List<String> dateList;
    private final List<Exercise> exerciseList;
    private final List<Workout> workoutList;
    private int currentJ;

    public SharedAdapter(Context context, List<String> dateList, List<Exercise> exerciseList, List<Workout> workoutList) {
        this.context = context;
        this.dateList = dateList;
        this.exerciseList = exerciseList;
        this.workoutList = workoutList;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view;
        if (viewType == TYPE_EXERCISE) {

            view = LayoutInflater.from(context).inflate(R.layout.post_exercise_item, viewGroup, false);
            return new PostExerciseViewHolder(view);

        } else {
            view = LayoutInflater.from(context).inflate(R.layout.post_workout_item, viewGroup, false);
            return new PostWorkoutViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_EXERCISE) {
            ((PostExerciseViewHolder) viewHolder).setDetails(exerciseList.get(currentJ));
        } else {
            ((PostWorkoutViewHolder) viewHolder).setDetails(workoutList.get(currentJ));
        }
    }

    //todo fix bug same exercise shows again and again
    @Override
    public int getItemCount() {
        return dateList.size();
    }

    @Override
    public int getItemViewType(int position) {
        for (int j = 0; j < workoutList.size(); j++) {
            Log.i(TAG, "getItemViewType: " + dateList.get(position) + "-----" + workoutList.get(j).getDate());
            if (dateList.get(position).equals(workoutList.get(j).getDate())) {
                currentJ = j;
                return TYPE_WORKOUT;
            }
        }
        //
        for (int j = 0; j < exerciseList.size(); j++) {
            Log.i(TAG, "getItemViewType: " + dateList.get(position) + "-----" + exerciseList.get(j).getDate());
            if (dateList.get(position).equals(exerciseList.get(j).getDate())) {
                currentJ = j;
                return TYPE_EXERCISE;
            }
        }
        //
        return 3;
    }

    private void getCreatorName(final String creatorId, final CallbackInterface callbackInterface) {
        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(creatorId)) {
                        callbackInterface.callbackMethod(ds.child("name").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private String formatDate(String mDate) {
        Date date = new Date(Long.parseLong(mDate));
        return DateFormat.getDateInstance().format(date);
    }

    private interface CallbackInterface {
        void callbackMethod(String name);
    }

    class PostExerciseViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textView)
        TextView textView;
        @BindView(R.id.exerciseNameEdittext)
        TextView exerciseNameEdittext;
        @BindView(R.id.exerciseImageView)
        CircleImageView exerciseImageView;
        Exercise currentExercise;


        public PostExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            //open specific exercise activity
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, SpecificExerciseActivity.class);
                    i.putExtra("exercise", currentExercise);
                    PostsActivity postsActivity = (PostsActivity) context;
                    postsActivity.startActivity(i);
                }
            });


        }

        public void setDetails(final Exercise exercise) {
            currentExercise = exercise;
            exerciseNameEdittext.setText(exercise.getName());
            getCreatorName(exercise.getCreatorId(), new CallbackInterface() {
                @Override
                public void callbackMethod(String name) {
                    textView.setText(name + " created new exercise on " + formatDate(exercise.getDate()));
                }
            });
            FirebaseStorage.getInstance().getReference("exerciseImages/").child(exercise.getPreviewPhoto1()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(itemView).load(uri).into(exerciseImageView);
                }
            });


        }
    }

    class PostWorkoutViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textViewWorkoutName)
        TextView textViewWorkoutName;
        @BindView(R.id.textView)
        TextView textView;
        @BindView(R.id.workoutImageView)
        ImageView workoutImageView;
        @BindView(R.id.textViewNumberOfExercises)
        TextView textViewNumberOfExercises;
        @BindView(R.id.textViewReps)
        TextView textViewReps;
        Workout currentWorkout;

        public PostWorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            //open specific workout activity
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, SpecificWorkoutActivity.class);
                    i.putExtra("workout", currentWorkout);
                    PostsActivity postsActivity = (PostsActivity) context;
                    postsActivity.startActivity(i);
                }
            });

        }

        public void setDetails(final Workout workout) {
            currentWorkout = workout;
            textViewWorkoutName.setText(workout.getName());
            textViewNumberOfExercises.setText(workout.getExercisesNumber());
            textViewReps.setText(workout.getDuration());
            //
            getCreatorName(workout.getCreatorId(), new CallbackInterface() {
                @Override
                public void callbackMethod(String name) {
                    textView.setText(name + " created new workout on " + formatDate(workout.getDate()));
                }
            });
            FirebaseStorage.getInstance().getReference().child(workout.getPhotoLink()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(itemView).load(uri).into(workoutImageView);
                }
            });

        }
    }
}
