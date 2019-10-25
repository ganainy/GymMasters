package com.example.myapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.Exercise;
import com.example.myapplication.model.SharedExerciseWorkout;
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
    List<SharedExerciseWorkout> sharedExerciseWorkoutList;

    public SharedAdapter(Context context, List<SharedExerciseWorkout> sharedExerciseWorkoutList) {
        this.context = context;
        this.sharedExerciseWorkoutList = sharedExerciseWorkoutList;

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
            ((PostExerciseViewHolder) viewHolder).setDetails(sharedExerciseWorkoutList.get(position).getExercise());
        } else {
            ((PostWorkoutViewHolder) viewHolder).setDetails(sharedExerciseWorkoutList.get(position).getWorkout());
        }
    }

    @Override
    public int getItemCount() {
        return sharedExerciseWorkoutList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (sharedExerciseWorkoutList.get(position).getEntityType() == 0) {
            return TYPE_EXERCISE;
        } else {
            return TYPE_WORKOUT;
        }

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
                    if (context != null) Glide.with(itemView).load(uri).into(exerciseImageView);
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
        @BindView(R.id.textViewDifficulty)
        TextView textViewDifficulty;

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
            textViewDifficulty.setText(workout.getLevel());
            //change bg color of difficultyTextView
            if (currentWorkout.getLevel().toLowerCase().equals("beginner")) {
                textViewDifficulty.setBackgroundResource(R.drawable.easy_bg);
            } else if (currentWorkout.getLevel().toLowerCase().equals("intermediate")) {
                textViewDifficulty.setBackgroundResource(R.drawable.intermediate_bg);
            } else if (currentWorkout.getLevel().toLowerCase().equals("professional")) {
                textViewDifficulty.setBackgroundResource(R.drawable.difficult_bg);
            }

//todo if user leaves activity image keeps downloading and glide crashes
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
                    if (context != null) Glide.with(itemView).load(uri).into(workoutImageView);
                }
            });

        }
    }
}
