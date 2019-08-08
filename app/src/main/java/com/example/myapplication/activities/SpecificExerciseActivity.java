package com.example.myapplication.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.Exercise;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SpecificExerciseActivity extends AppCompatActivity {
    Exercise exercise;

    @BindView(R.id.exo_playerview)
    PlayerView simpleExoPlayerView;

    @BindView(R.id.executionTextView)
    TextView executionTextView;

    @BindView(R.id.preparationTextView)
    TextView preparationTextView;

    @BindView(R.id.mechanicTextView)
    TextView mechanicTextView;

    @BindView(R.id.utilityTextView)
    TextView utilityTextView;

    @BindView(R.id.exerciseImageView)
    ImageView exerciseImageView;

    @BindView(R.id.showVideoFAB)
    FloatingActionButton showVideoFAB;
    private boolean b;

    @OnClick(R.id.showVideoFAB)
    void hidePhotoShowVideo() {
        showVideoFAB.hide();
        exerciseImageView.setVisibility(View.INVISIBLE);
        showVideoFromStorage(exercise.getVideoLink());
    }


    @OnClick(R.id.mechanicInfo)
    void showMechanicInfo() {
        showAlertDialog(mechanicTextView.getText().toString().toLowerCase());
    }


    @OnClick(R.id.utilityInfo)
    void showUtilityInfo() {
        showAlertDialog(utilityTextView.getText().toString().toLowerCase());
    }

    private static final String TAG ="SpecificExerciseActivit" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_exercise);
        ButterKnife.bind(this);

        //get exercise info from clicked exercise in adapter
        Intent i = getIntent();
        exercise = i.getParcelableExtra("exercise");

        //change name in actionbar
        setTitle(exercise.getName());

        executionTextView.setText(exercise.getExecution());
        preparationTextView.setText(exercise.getPreparation());
        mechanicTextView.setText(exercise.getMechanism());
        utilityTextView.setText(exercise.getUtility());

        //test
        downloadPreviewImage2(new CallbackInterface() {
            @Override
            public void callbackMethod(Exercise exercise) {
                switchExercisePhotos(exercise);
            }
        });


    }

    private void downloadPreviewImage2(final CallbackInterface callbackInterface) {

        StorageReference storageRef2 = FirebaseStorage.getInstance().getReference().child(exercise.getPreviewPhoto2());
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(exercise.getPreviewPhoto1());
        File localFile2 = null;
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
            localFile2 = File.createTempFile("images", "jpg");

            final File finalLocalFile = localFile;
            final File finalLocalFile2 = localFile2;

            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been createed
                    exercise.setPreviewBitmap(BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath()));

                    callbackInterface.callbackMethod(exercise);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void switchExercisePhotos(Exercise exercise) {

        //switch exercise photos every 1.5 seconds
        Timer timer = new Timer();
        TimerTask t = new TimerTask() {
            @Override
            public void run() {

                if (!b) {
                    b = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            exerciseImageView.setImageBitmap(SpecificExerciseActivity.this.exercise.getPreviewBitmap());
                        }
                    });

                } else {
                    b = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            exerciseImageView.setImageBitmap(SpecificExerciseActivity.this.exercise.getPreview2Bitmap());
                        }
                    });

                }
            }
        };
        timer.scheduleAtFixedRate(t, 0, 1500);
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


    private void showVideoFromStorage(String videoLink) {

        //show hidden view then play video
        simpleExoPlayerView.setVisibility(View.VISIBLE);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference pathReference = storageRef.child(videoLink);
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                TrackSelector trackSelector = new DefaultTrackSelector();

                SimpleExoPlayer exoPlayer = ExoPlayerFactory.newSimpleInstance(SpecificExerciseActivity.this, trackSelector);

                simpleExoPlayerView.setPlayer(exoPlayer);

                exoPlayer.setPlayWhenReady(true);

                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(SpecificExerciseActivity.this, Util.getUserAgent(SpecificExerciseActivity.this, "VideoPlayer"));

                MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);

                exoPlayer.prepare(videoSource);
                exoPlayer.setPlayWhenReady(true);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                FancyToast.makeText(SpecificExerciseActivity.this, "Unable to play video", Toast.LENGTH_SHORT, 3, false).show();
                Log.i(TAG, exception.getMessage());
            }
        });


    }


    private interface CallbackInterface {
        void callbackMethod(Exercise exercise);
    }


}
