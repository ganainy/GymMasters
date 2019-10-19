package com.example.myapplication.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.model.Exercise;
import com.example.myapplication.youtube_model.Example;
import com.example.myapplication.youtube_model.Id;
import com.example.myapplication.youtube_model.Item;
import com.example.myapplication.youtube_model.YoutubeApi;
import com.example.myapplication.youtube_model.YoutubeConfig;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SpecificExerciseActivity extends YouTubeBaseActivity {
    private static final String TAG = "hehe";


    @BindView(R.id.exerciseImageView)
    ImageView exerciseImageView;

    @BindView(R.id.youTubePlayerView)
    YouTubePlayerView youTubePlayerView;

    @BindView(R.id.loadingImageProgressBar)
    ProgressBar loadingImageProgressBar;


    @BindView(R.id.nameTextView)
    TextView nameTextView;
    @BindView(R.id.executionTextView)
    TextView executionTextView;
    @BindView(R.id.additionalNotesTextView)
    TextView additionalNotesTextView;
    @BindView(R.id.mechanicTextView)
    TextView mechanicTextView;
    @BindView(R.id.targetedMuscleTextView)
    TextView targetedMuscleTextView;

    private boolean b;
    private Timer timer;
    public Exercise exercise;
    private String exerciseName;
    YouTubePlayer.OnInitializedListener onInitializedListener;
    private List<String> videoPlaylist = new ArrayList<>();
    private ScrollView parent;
    private RequestBuilder<Drawable> load, load2;


    @OnClick(R.id.mechanicQuestionMark)
    void showMechanicInfo() {
        showAlertDialog(mechanicTextView.getText().toString().toLowerCase());
    }


    private String targetMuscle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_exercise);
        ButterKnife.bind(this);


        Intent i = getIntent();
        if (i.hasExtra("name")) { //if true it means exercise is coming from inside a workout
            exerciseName = i.getStringExtra("name");
            targetMuscle = i.getStringExtra("targetMuscle");

            downloadExercise(new CallbackInterface() {
                @Override
                public void callbackMethod(Exercise exercisee) {
                    exercisee.setBodyPart(targetMuscle);
                    exercise = exercisee;
                    showInViews();
                }
            });
        } else if (i.hasExtra("exercise")) {//if true it means exercise is coming from Exercise adapter of ExerciseActivity --or shared adapter of posts activity
            // exercise=new Exercise();
            exercise = i.getParcelableExtra("exercise");
            showInViews();
        }


        /**snack bar to ask user if he wants video instead of photos*/
        parent = findViewById(R.id.parent);
        showFirstSnackbar();


    }

    private void showFirstSnackbar() {
        Snackbar snackbar = Snackbar
                .make(parent, "Show video instead?", Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        exerciseImageView.setVisibility(View.GONE);
                        showYoutubePlayer();


                        showSecondSnackbar();
                    }
                });
        snackbar.show();
    }

    private void showSecondSnackbar() {
        /**snack bar to ask user if he wants photos instead of video again*/
        Snackbar snackbar = Snackbar.make(parent, "Return to showing images?", Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showFirstSnackbar();
                        showSwitchingPhotosAgain();
                    }
                });
        snackbar.show();
    }

    private void showInViews() {
        nameTextView.setText(exercise.getName());
        executionTextView.setText(exercise.getExecution());
        mechanicTextView.setText(exercise.getMechanism());
        targetedMuscleTextView.setText(exercise.getBodyPart());

        if (exercise.getAdditional_notes().equals("")) additionalNotesTextView.setText("None");
        else additionalNotesTextView.setText(exercise.getAdditional_notes());

        downloadPreviewImage();
    }

    private void downloadExercise(final CallbackInterface callbackInterface) {
        exercise = new Exercise();
        DatabaseReference exerciseNode = FirebaseDatabase.getInstance().getReference("excercises").child(targetMuscle);
        exerciseNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("name").getValue().equals(exerciseName)) {
                        exercise.setExecution(ds.child("execution").getValue().toString());
                        if (ds.hasChild("additional_notes"))
                            exercise.setAdditional_notes(ds.child("additional_notes").getValue().toString());
                        exercise.setMechanism(ds.child("mechanism").getValue().toString());
                        exercise.setPreviewPhoto1(ds.child("previewPhoto1").getValue().toString());
                        exercise.setPreviewPhoto2(ds.child("previewPhoto2").getValue().toString());


                    }
                }
                callbackInterface.callbackMethod(exercise);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "onCancelled: " + databaseError.getMessage() + " %%% " + databaseError.getDetails());

            }
        });


    }

    private void downloadPreviewImage() {

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("exerciseImages/").child(exercise.getPreviewPhoto1());

        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                load = Glide.with(getApplicationContext()).load(uri);
                downloadPreviewImage2(load);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "glideError: " + e.getMessage() + exercise.getPreviewPhoto1());
                exerciseImageView.setImageResource(R.drawable.ic_error_black_24dp);
                loadingImageProgressBar.setVisibility(View.GONE);
            }
        });
//

    }

    private void downloadPreviewImage2(final RequestBuilder<Drawable> load) {
        StorageReference storageRef2 = FirebaseStorage.getInstance().getReference().child("exerciseImages/").child(exercise.getPreviewPhoto2());
        storageRef2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                load2 = Glide.with(getApplicationContext()).load(uri);
                switchExercisePhotos();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "glideError: " + e.getMessage());
                /**if downloading second image failed just preview first one*/
                loadingImageProgressBar.setVisibility(View.GONE);
                load.into(exerciseImageView);
            }
        });

    }

    private void switchExercisePhotos() {

        if (load == null || load2 == null) {
            return;
        }
        loadingImageProgressBar.setVisibility(View.GONE);
        //switch exercise photos every 1.5 seconds
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                if (!b) {
                    b = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            load.into(exerciseImageView);
                        }
                    });

                } else {
                    b = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            load2.into(exerciseImageView);
                        }
                    });

                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1500);

    }


    private void showAlertDialog(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false)
                .setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Do nothing
                    }
                });

        //change message depending on type of mechanism , utility
        switch (s) {
            case "basic": {
                builder.setMessage("\n" +
                        "\n" +
                        "    A principal exercise that can place greater absolute intensity on muscles exercised relative to auxiliary exercises. Basic exercises tend to have more of the following characteristics:\n" +
                        "    gravity dependent\n" +
                        "    inclusion or shift of resistance through multiple muscle group throughout the range of motion\n" +
                        "        e.g. bench press: front deltoid to pectoralis major to triceps\n" +
                        "    natural transfer of torsion force to compression force (e.g., lockout on squat, bench press, etc.) or tension force (e.g. extension of arm curl) to the bone(s) and joint(s) during full range of motion\n" +
                        "        Also see angle of pull\n");
                break;
            }
            case "compound": {
                builder.setMessage("An exercise that involves two or more joint movements. ");
                break;
            }
            case "auxiliary": {
                builder.setMessage("An optional exercise that may supplement a basic exercise. Auxiliary exercises may place greater relative intensity on a specific muscle or a head of a muscle.");
                break;
            }
            case "isolated": {
                builder.setMessage("An exercise that involves just one discernible joint movement. ");
                break;
            }
            default:
                builder.setMessage("No info about that yet, Sorry. ");

        }


        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        //alert.setTitle("Additional information");
        alert.show();

    }


    private void showYoutubePlayer() {
        loadingImageProgressBar.setVisibility(View.GONE);
        //stop timer from working in background
        if (timer != null)
            timer.cancel();


        //get video id for the exercise name using youtube data api
        //exercise.getName() might be null if iam coming from inside a workout so i will use exerciseName instead
        retrofit(exercise.getName() != null ? exercise.getName() : exerciseName);

        //show hidden view then play video
        youTubePlayerView.setVisibility(View.VISIBLE);
        //init youtube player
        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.i(TAG, "onInitializationSuccess: ");
                if (videoPlaylist.size() > 1) youTubePlayer.loadVideos(videoPlaylist);
                else openAlertDialog();
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.i(TAG, "onInitializationFailure: ");
                openAlertDialog();
            }
        };


    }

    private void openAlertDialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setMessage("In app play failed ,Show video inside youtube app instead?").setTitle("Error playing video :/");


        builder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=" + exercise.getName()));
                        startActivity(intent);
                        showSwitchingPhotosAgain();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  show swapping photos again and hide videoplayer
                        showSwitchingPhotosAgain();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.show();
    }

    private void showSwitchingPhotosAgain() {
        //  show swapping photos again and hide youtube player
        youTubePlayerView.setVisibility(View.INVISIBLE);
        exerciseImageView.setVisibility(View.VISIBLE);
        switchExercisePhotos();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
        }
    }

    private interface CallbackInterface {
        void callbackMethod(Exercise exercise);
    }


    private void retrofit(String q) {
        String mBaseUrl = "https://www.googleapis.com/youtube/v3/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(GsonConverterFactory.create()).build();

        YoutubeApi youtubeApi = retrofit.create(YoutubeApi.class);


        /** hidden api key, replace with your key that supports youtube data api for youtube playing videos feature to work**/
        Call<Example> call = youtubeApi.getParentObject("snippet", q, "video", BuildConfig.API_KEY);
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                if (response.isSuccessful()) {
                    Example body = response.body();
                    List<Item> items = body.getItems();
                    for (Item item : items) {
                        Id id = item.getId();
                        String videoId = id.getVideoId();
                        videoPlaylist.add(videoId);
                        Log.i(TAG, "onResponse: " + videoId);
                    }

                    youTubePlayerView.initialize(YoutubeConfig.getApiKey(), onInitializedListener);

                } else {
                    Log.i(TAG, "onResponse: " + response.code() + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Log.i(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


}


