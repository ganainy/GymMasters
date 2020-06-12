package ganainy.dev.gymmasters.ui.createExercise;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.utils.MyConstants;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.google.android.material.snackbar.Snackbar;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static ganainy.dev.gymmasters.ui.main.exercises.MainFragmentExcercies.ABS;
import static ganainy.dev.gymmasters.ui.main.exercises.MainFragmentExcercies.BACK;
import static ganainy.dev.gymmasters.ui.main.exercises.MainFragmentExcercies.BICEPS;
import static ganainy.dev.gymmasters.ui.main.exercises.MainFragmentExcercies.CARDIO;
import static ganainy.dev.gymmasters.ui.main.exercises.MainFragmentExcercies.CHEST;
import static ganainy.dev.gymmasters.ui.main.exercises.MainFragmentExcercies.LOWERLEG;
import static ganainy.dev.gymmasters.ui.main.exercises.MainFragmentExcercies.SHOULDER;
import static ganainy.dev.gymmasters.ui.main.exercises.MainFragmentExcercies.SHOWALL;
import static ganainy.dev.gymmasters.ui.main.exercises.MainFragmentExcercies.TRICEPS;

public class CreateNewExerciseActivity extends AppCompatActivity {
    private static final String TAG = "CreateNewExerciseActivi";
    private static final int PICK_IMAGE = 101;
    private static final int PICK_IMAGE2 = 102;
    String newExerciseSelectedMuscle;
    String newExerciseMechanic;


    @BindView(R.id.bodyPartSpinner)
    Spinner bodyPartSpinner;

    @BindView(R.id.mechanicSpinner)
    Spinner mechanicSpinner;


    @BindView(R.id.nameEditText)
    TextView nameEditText;


    @BindView(R.id.executionEditText)
    TextView executionEditText;


    @BindView(R.id.additionalNotesEditText)
    TextView additionalNotesEditText;

    @BindView(R.id.workoutImageView)
    ImageView addExercisePhoto;

    @BindView(R.id.addExercisePhoto2)
    ImageView addExercisePhoto2;
    @BindView(R.id.parentScroll)
    ScrollView parentScroll;
    @BindView(R.id.loadingLayout)
    ConstraintLayout loadingLayout;
    @BindView(R.id.circle_progress)
    CircleProgress circleProgress;


    private Uri imageUri, image2Uri;
    private CreateNewExerciseViewModel createNewExerciseViewModel;
    private Observer<Boolean> mObserver;
    private int fakeProgress;

    @OnClick(R.id.saveButton)
    void saveExercise() {

        if (nameEditText.getText().length() < 6) {
            FancyToast.makeText(CreateNewExerciseActivity.this, "Exercise name must be at least 6 letters", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
        } else if (executionEditText.getText().length() < 6) {
            FancyToast.makeText(CreateNewExerciseActivity.this, "Exercise execution must be at least 6 letters", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
        } else if (newExerciseMechanic.equals("")) {
            FancyToast.makeText(CreateNewExerciseActivity.this, "Press arrow to select mechanic", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
        } else if (newExerciseSelectedMuscle.equals("")) {
            FancyToast.makeText(CreateNewExerciseActivity.this, "Press arrow to select targeted muscle", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
        } else if (imageUri == null || image2Uri == null) {
            FancyToast.makeText(CreateNewExerciseActivity.this, "Please select two photos describing exercise movement.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
        } else {

            //show loading layout
            loadingLayout.setVisibility(View.VISIBLE);

            /**add time in millis with image name to make it unique*/
            Date date = new Date();
            final long timeMilli = date.getTime();


            final Exercise exercise = new Exercise(nameEditText.getText().toString(), newExerciseSelectedMuscle, executionEditText.getText().toString()
                    , newExerciseMechanic, imageUri.getLastPathSegment() + timeMilli, image2Uri.getLastPathSegment() + timeMilli);

            exercise.setCreatorId(MyConstants.loggedInUserId);

            if (!additionalNotesEditText.getText().equals(""))
                exercise.setAdditional_notes(additionalNotesEditText.getText().toString());
            exercise.setDate(String.valueOf(System.currentTimeMillis()));


            //view model
            createNewExerciseViewModel = ViewModelProviders.of(this).get(CreateNewExerciseViewModel.class);
            mObserver = new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean isExerciseNameRepeated) {
                    if (isExerciseNameRepeated) {
                        FancyToast.makeText(getApplicationContext(), "Exercise with same name already exists \n your exercise was not added",
                                FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

                        loadingLayout.setVisibility(View.GONE);

                    } else {
                        //name is not repaeated
                        createNewExerciseViewModel.uploadExercisePhotos(imageUri, image2Uri, timeMilli).observe
                                (CreateNewExerciseActivity.this, new Observer<Integer>() {
                                    @Override
                                    public void onChanged(final Integer progress) {


                                        //add little delay when updating progress to look smoother
                                        new Thread() {
                                            public void run() {
                                                while (fakeProgress < progress) {
                                                    try {
                                                        runOnUiThread(new Runnable() {

                                                            @Override
                                                            public void run() {
                                                                fakeProgress++;
                                                                circleProgress.setProgress(fakeProgress);
                                                            }
                                                        });
                                                        Thread.sleep(15);
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }.start();




                                        if (progress == 100) { //means both images uploaded successfully
                                            createNewExerciseViewModel.uploadExercise(exercise).
                                                    observe(CreateNewExerciseActivity.this, new Observer<Boolean>() {
                                                        @Override
                                                        public void onChanged(Boolean isExerciseUploaded) {
                                                            if (isExerciseUploaded) {
                                                                FancyToast.makeText(CreateNewExerciseActivity.this, "Added Exercise successfully.", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                                                                finish();
                                                            } else {
                                                                loadingLayout.setVisibility(View.GONE);
                                                                FancyToast.makeText(CreateNewExerciseActivity.this, "Something went wrong, check connection and try again.", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                                                                //error happened while  adding exercise
                                                            }
                                                        }
                                                    });
                                        }
                                    }

                                });
                    }
                }
            };


            /**check if there is an exercise with same name in DB exists*/
            createNewExerciseViewModel.checkRepeation(exercise.getName()).observe(this, mObserver);

        }
   /*    Exercise exercise = new Exercise();
        exercise.setCreatorId("KMo0drWBZNNK1UpriR96G9NXEr32"); //admin id
        exercise.setName("Machine-assisted Chest Dip (kneeling)");
        exercise.setBodyPart("Chest");
        exercise.setExecution("Lower body by bending arms, allowing elbows to flare out to sides. When slight stretch is felt in chest or shoulders push body up until arms are straight. Repeat.");
        exercise.setMechanism("Compound");
        exercise.setPreviewPhoto1("dip_kneeling.png");
        exercise.setPreviewPhoto2("dip_kneeling2.png");
        exercise.setDate(String.valueOf(System.currentTimeMillis()));
*/


    }


    @OnClick(R.id.workoutImageView)
    void getPhotoFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @OnClick(R.id.addExercisePhoto2)
    void getPhoto2FromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_exercise);
        ButterKnife.bind(this);


        Snackbar snackbar = Snackbar.make(parentScroll, "Fields with orange dots are mandatory", Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        snackbar.show();

        muscleSpinnerCode();
        mechanicSpinnerCode();
    }


    private void mechanicSpinnerCode() {
        final String[] mechanic = {"", "Compound", "Isolated"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mechanic);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        mechanicSpinner.setAdapter(arrayAdapter);
        mechanicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                newExerciseMechanic = mechanic[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void muscleSpinnerCode() {

        final String[] muscles = {"", "Chest", "Triceps", "Shoulders", "Biceps", "Abs", "Back", "Leg", "Cardio", "Other"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, muscles);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        bodyPartSpinner.setAdapter(arrayAdapter);
        bodyPartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int selectedIndex, long l) {
                switch (selectedIndex){
                    case 1:
                    newExerciseSelectedMuscle = CHEST;
                    break;
                    case 2:
                        newExerciseSelectedMuscle = TRICEPS;
                        break;
                    case 3:
                        newExerciseSelectedMuscle = SHOULDER;
                        break;
                    case 4:
                        newExerciseSelectedMuscle = BICEPS;
                        break;
                    case 5:
                        newExerciseSelectedMuscle = ABS;
                        break;
                    case 6:
                        newExerciseSelectedMuscle = BACK;
                        break;
                    case 7:
                        newExerciseSelectedMuscle = LOWERLEG;
                        break;
                    case 8:
                        newExerciseSelectedMuscle = CARDIO;
                        break;
                    case 9:
                        newExerciseSelectedMuscle = SHOWALL;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        if (requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            addExercisePhoto.setImageURI(imageUri);
        }
        if (requestCode == PICK_IMAGE2) {
            image2Uri = data.getData();
            addExercisePhoto2.setImageURI(image2Uri);
        }
    }


    @OnClick(R.id.backArrowImageView)
    public void onViewClicked() {
        super.onBackPressed();
    }


}
