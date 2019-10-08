package com.example.myapplication.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.model.Exercise;
import com.example.myapplication.utils.MyConstant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shashank.sony.fancytoastlib.FancyToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateNewExerciseActivity extends AppCompatActivity {
    private static final String TAG = "CreateNewExerciseActivi";
    private static final int PICK_IMAGE = 101;
    private static final int PICK_IMAGE2 = 102;
    String newExerciseSelectedMuscle;
    String newExerciseUtility;
    String newExerciseMechanic;


    @BindView(R.id.bodyPartSpinner)
    Spinner bodyPartSpinner;

    @BindView(R.id.mechanicSpinner)
    Spinner mechanicSpinner;

    @BindView(R.id.utilitySpinner)
    Spinner utilitySpinner;

    @BindView(R.id.nameTextView)
    TextView nameTextView;


    @BindView(R.id.executionTextView)
    TextView executionTextView;


    @BindView(R.id.preparationTextView)
    TextView preparationTextView;

    @BindView(R.id.addExercisePhoto)
    ImageView addExercisePhoto;

    @BindView(R.id.addExercisePhoto2)
    ImageView addExercisePhoto2;


    private Uri imageUri, image2Uri;

    @OnClick(R.id.saveButton)
    void saveExercise() {

        if (nameTextView.getText().length() < 6 || executionTextView.getText().length() < 6 || preparationTextView.getText().length() < 6) {
            FancyToast.makeText(CreateNewExerciseActivity.this, "Please fill fields with minimum of 6 letters each.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

        } else if (imageUri == null || image2Uri == null) {
            FancyToast.makeText(CreateNewExerciseActivity.this, "Please select two photos describing exercise movement.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
        } else {
            uploadExercisePhotos();
            Exercise exercise = new Exercise(nameTextView.getText().toString(), newExerciseSelectedMuscle,
                    executionTextView.getText().toString(), preparationTextView.getText().toString(),
                    newExerciseMechanic, newExerciseUtility, imageUri.getLastPathSegment(), image2Uri.getLastPathSegment(), "no_video");
            exercise.setCreatorId(MyConstant.loggedInUserId);
            exercise.setDate(String.valueOf(System.currentTimeMillis()));
            uploadExercise(exercise);

        }
      /*  Exercise exercise = new Exercise();
        exercise.setCreatorId("KMo0drWBZNNK1UpriR96G9NXEr32");
        exercise.setName("Machine-assisted Chest Dip (kneeling)");
        exercise.setBodyPart("Chest");
        exercise.setExecution("Lower body by bending arms, allowing elbows to flare out to sides. When slight stretch is felt in chest or shoulders push body up until arms are straight. Repeat.");
        exercise.setPreparation("Mount wide dip bar with oblique grip (bar dia" +
                "gonal under palm), arms straight with shoulders above hands. Kneel on padded platform, lowering it down slightly so hips are slightly bent.");
        exercise.setMechanism("Compound");
        exercise.setUtility("Basic");
        exercise.setPreviewPhoto1("dip_kneeling.png");
        exercise.setPreviewPhoto2("dip_kneeling2.png");
        exercise.setVideoLink("dip_kneeling.mp4");
        exercise.setDate(String.valueOf(System.currentTimeMillis()));
        uploadExercise(exercise);


        Exercise exercise2 = new Exercise();
        exercise2.setCreatorId("KMo0drWBZNNK1UpriR96G9NXEr32");
        exercise2.setName("Barbell Bench Press");
        exercise2.setBodyPart("Chest");
        exercise2.setExecution("Lower weight to chest. Press bar upward until arms are extended. Repeat.");
        exercise2.setPreparation("Lie supine on bench. Dismount barbell from rack over upper chest using wide oblique overhand grip.");
        exercise2.setMechanism("Compound");
        exercise2.setUtility("Basic");
        exercise2.setPreviewPhoto1("BBBenchPress.png");
        exercise2.setPreviewPhoto2("BBBenchPress2.png");
        exercise2.setVideoLink("BBBenchPress.mp4");
        exercise.setDate(String.valueOf(System.currentTimeMillis()));
        uploadExercise(exercise2);



        Exercise exercise3= new Exercise();
        exercise3.setCreatorId("KMo0drWBZNNK1UpriR96G9NXEr32");
        exercise3.setName("Barbell Decline Bench Press");
        exercise3.setBodyPart("Chest");
        exercise3.setExecution("Lower weight to chest. Press bar until arms are extended. Repeat.");
        exercise3.setPreparation("Lie supine on decline bench with feet under leg brace. Dismount barbell from rack over chest using wide oblique overhand grip.");
        exercise3.setMechanism("Compound");
        exercise3.setUtility("Auxiliary");
        exercise3.setPreviewPhoto1("BarbellDeclineBenchPress.png");
        exercise3.setPreviewPhoto2("BarbellDeclineBenchPress2.png");
        exercise3.setVideoLink("BarbellDeclineBenchPress.mp4");
        exercise.setDate(String.valueOf(System.currentTimeMillis()));
        uploadExercise(exercise3);



        Exercise exercise4= new Exercise();
        exercise4.setCreatorId("KMo0drWBZNNK1UpriR96G9NXEr32");
        exercise4.setName("Machine-assisted Chest Dip (kneeling)");
        exercise4.setBodyPart("Chest");
        exercise4.setExecution("Lower body by bending arms, allowing elbows to flare out to sides. When slight stretch is felt in chest or shoulders push body up until arms are straight. Repeat.");
        exercise4.setPreparation("Mount wide dip bar with oblique grip (bar dia" +
                "gonal under palm), arms straight with shoulders above hands. Kneel on padded platform, lowering it down slightly so hips are slightly bent.");
        exercise4.setMechanism("Compound");
        exercise4.setUtility("Basic");
        exercise4.setPreviewPhoto1("dip_kneeling.png");
        exercise4.setPreviewPhoto2("dip_kneeling2.png");
        exercise4.setVideoLink("dip_kneeling.mp4");
        exercise.setDate(String.valueOf(System.currentTimeMillis()));
        uploadExercise(exercise4);




        Exercise exercise5= new Exercise();
        exercise5.setCreatorId("KMo0drWBZNNK1UpriR96G9NXEr32");
        exercise5.setName("Cable Decline Fly");
        exercise5.setBodyPart("Chest");
        exercise5.setExecution("Bring cable stirrups together above upper abdomen in hugging motion; elbows in fixed position and shoulders internally rotated so elbows are to sides. Return to starting position until chest is slightly stretched. Repeat.");
        exercise5.setPreparation("Grasp two opposing high pulley dumbbell attachments. Lie supine on decline bench, in middle and perpendicular to both pulleys. Slightly bend elbows and internally rotate shoulders so elbows are back.");
        exercise5.setMechanism("Isolated");
        exercise5.setUtility("Auxiliary");
        exercise5.setPreviewPhoto1("CableDeclineFly.png");
        exercise5.setPreviewPhoto2("CableDeclineFly2.png");
        exercise5.setVideoLink("CableDeclineFly.mp4");
        exercise.setDate(String.valueOf(System.currentTimeMillis()));
        uploadExercise(exercise5);*/


    }

    private void uploadExercise(Exercise exercise) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Task<Void> excercises = reference.child("excercises").child(exercise.getBodyPart().toLowerCase()).push().setValue(exercise);
        excercises.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FancyToast.makeText(CreateNewExerciseActivity.this, "Added successfully.", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                finish();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                FancyToast.makeText(CreateNewExerciseActivity.this, "Something went wrong , check connection and try again.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                Log.i(TAG, "onFailure: " + e.getMessage());
            }
        });

    }

    @OnClick(R.id.addExercisePhoto)
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

        muscleSpinnerCode();
        mechanicSpinnerCode();
        utilitySpinnerCode();
    }

    private void utilitySpinnerCode() {
        final String[] utility = {"Basic", "Auxiliary"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, utility);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        utilitySpinner.setAdapter(arrayAdapter);
        utilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                newExerciseUtility = utility[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void mechanicSpinnerCode() {
        final String[] mechanic = {"Compound", "Isolated"};
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
        final String[] muscles = {"Chest", "Triceps", "Shoulders", "Biceps", "Abs", "Back", "Forearm", "Upper leg", "Glutes", "Cardio", "Lower leg", "Other"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, muscles);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        bodyPartSpinner.setAdapter(arrayAdapter);
        bodyPartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                newExerciseSelectedMuscle = muscles[i];
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


    private void uploadExercisePhotos() {
        // Create a storage reference from our app
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference imagesRef = storageRef.child("exerciseImages/" + imageUri.getLastPathSegment());
        imagesRef.putFile(imageUri);
        final StorageReference imagesRef2 = storageRef.child("exerciseImages/" + image2Uri.getLastPathSegment());
        imagesRef2.putFile(image2Uri);

    }



}
