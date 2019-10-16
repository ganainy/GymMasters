package com.example.myapplication.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TimerActivity extends AppCompatActivity implements Chronometer.OnChronometerTickListener {
    private static final String TAG = "TimerActivity";
    @BindView(R.id.chronometer)
    Chronometer chronometer;
    @BindView(R.id.imageView)
    ImageView startPause;
    @BindView(R.id.view)
    View reset;
    @BindView(R.id.view2)
    View stopWatch;
    @BindView(R.id.textView4)
    TextView secsTextView;
    @BindView(R.id.textView5)
    TextView minsTextView;
    int secs, mins;
    private boolean isRunning;
    private long pauseOffset;
    private int choosenTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    @OnClick({R.id.imageView, R.id.view, R.id.view2, R.id.backArrowImageView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imageView:
                /**play or pause chronometer*/
                playPauseChronometer();

                break;
            case R.id.view:
                /**reset chronometer*/
                resetChronometer();
                break;
            case R.id.view2:
                /**prompt user to choose time and play audio when finished*/
                openSetTimerAlertDialog();
                break;
            case R.id.backArrowImageView:
                onBackPressed();
                break;
        }
    }


    private void openSetTimerAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.secs_minutes_layout, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the
        // dialog layout
        builder.setTitle("Choose timer duration");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_reset_timer);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        checkDeviceVoiceLevel();
                        resetChronometer();
                        playPauseChronometer();
                        chronometer.setOnChronometerTickListener(TimerActivity.this);
                        convertSetTimeToMillis();

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
        final NumberPicker secsPicker = view.findViewById(R.id.secsPicker);
        NumberPicker minsPicker = view.findViewById(R.id.minsPicker);

        secsPicker.setMinValue(1);
        secsPicker.setMaxValue(59);

        minsPicker.setMinValue(0);
        minsPicker.setMaxValue(59);


        secsPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                secs = numberPicker.getValue();
                Log.i(TAG, "onValueChange: " + secs);
            }
        });

        minsPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mins = numberPicker.getValue();
                Log.i(TAG, "onValueChange: " + mins);
            }
        });
    }

    private void checkDeviceVoiceLevel() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolumePercentage = 100 * currentVolume / maxVolume;

        Log.i(TAG, "checkDeviceVoiceLevel: " + currentVolumePercentage);
        if (currentVolumePercentage < 50) {
            Toast.makeText(this, "Audio level is less than 50% \n,Consider increasing volume to hear the reminder clearly", Toast.LENGTH_LONG).show();
        }
    }

    private void convertSetTimeToMillis() {
        choosenTime = (secs + (60 * mins)) * 1000;
    }

    @Override
    public void onChronometerTick(Chronometer chronometer) {
        if (choosenTime > 0 && (SystemClock.elapsedRealtime() - chronometer.getBase()) > choosenTime) {
            Log.i(TAG, "onChronometerTick2: " + choosenTime + "---" + (SystemClock.elapsedRealtime() - chronometer.getBase()) + "gamed");
            playAudio();
            resetChronometer();
        }
    }


    private void playPauseChronometer() {
        if (!isRunning) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            isRunning = true;
            startPause.setImageResource(R.drawable.ic_pause_timer);
        } else {
            chronometer.stop();
            isRunning = false;
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            startPause.setImageResource(R.drawable.ic_play_timer);
        }
    }


    private void resetChronometer() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.stop();
        pauseOffset = 0;
        startPause.setImageResource(R.drawable.ic_play_timer);
        isRunning = false;
    }

    private void playAudio() {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
        mediaPlayer.start();
        /**remove listener so music won't play again unless i set it again using the button*/
        chronometer.setOnChronometerTickListener(null);
    }


    //todo move chronometer and audio to a service to keep chronometer running in bg and voice works even if user left app
}
