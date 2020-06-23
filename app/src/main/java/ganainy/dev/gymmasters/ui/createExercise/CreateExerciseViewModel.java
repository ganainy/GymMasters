package ganainy.dev.gymmasters.ui.createExercise;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.utils.AuthUtils;
import ganainy.dev.gymmasters.utils.Event;
import ganainy.dev.gymmasters.utils.NetworkState;

import static ganainy.dev.gymmasters.ui.exercise.ExercisesViewModel.EXERCISES;
import static ganainy.dev.gymmasters.utils.Constants.SOCIAL;
import static ganainy.dev.gymmasters.utils.MiscellaneousUtils.formatUriAsTimeStampedString;

public class CreateExerciseViewModel extends ViewModel {
    private Application app;
    private static final String TAG = "CreateExerciseViewModel";
    public static final String NAME = "name";
    public static final String EXERCISE_IMAGES = "exerciseImages/";
    private String exerciseSelectedMuscle;
    private String exerciseMechanic;
    private Uri firstImageUri;
    private Uri secondImageUri;
    private Exercise exercise;

    public CreateExerciseViewModel(Application app) {
        this.app = app;

        repeatedNameTransformation = Transformations.map(repeatedNameLiveData, isRepeatedName -> {
            Log.d(TAG, "CreateExerciseViewModel: repeatedNameTransformation");
            if (!isRepeatedName)
                uploadFirstExercisePhoto();
            return isRepeatedName;
        });


        firstImageUploadedTransformation = Transformations.map(firstImageDownloadUrlLiveData, firstImageDownloadUrl -> {
            Log.d(TAG, "CreateExerciseViewModel: firstImageUploadedTransformation");
            exercise.setPreviewPhotoOneUrl(firstImageDownloadUrl.toString());
            uploadSecondExercisePhoto();
            return firstImageDownloadUrl;
        });


        secondImageUploadedTransformation = Transformations.map(secondImageDownloadUriLiveData, secondImageDownloadUri -> {
            Log.d(TAG, "CreateExerciseViewModel: secondImageUploadedTransformation");
            exercise.setPreviewPhotoTwoUrl(secondImageDownloadUri.toString());
            uploadExercise(exercise);
            return secondImageDownloadUri;
        });
    }


    private LiveData<Boolean> repeatedNameTransformation;
    private MutableLiveData<Boolean> repeatedNameLiveData = new MutableLiveData<>();
    private MutableLiveData<Event<ExerciseFieldIssue>> missingFieldLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> uploadProgressLiveData = new MutableLiveData<>();
    private MutableLiveData<NetworkState> networkStateLiveData = new MutableLiveData<>();
    private MutableLiveData<Uri> firstImageUriLiveData = new MutableLiveData<>();
    private MutableLiveData<Uri> firstImageDownloadUrlLiveData = new MutableLiveData<>();
    private MutableLiveData<Uri> secondImageUriLiveData = new MutableLiveData<>();
    private MutableLiveData<Uri> secondImageDownloadUriLiveData = new MutableLiveData<>();
    private LiveData<Uri> firstImageUploadedTransformation;

    public LiveData<Uri> getSecondImageUploadedTransformation() {
        return secondImageUploadedTransformation;
    }

    private LiveData<Uri> secondImageUploadedTransformation;

    public LiveData<Uri> getFirstImageUriLiveData() {
        return firstImageUriLiveData;
    }

    public LiveData<Uri> getSecondImageUriLiveData() {
        return secondImageUriLiveData;
    }


    public LiveData<Uri> getFirstImageUploadedTransformation() {
        return firstImageUploadedTransformation;
    }

    public void setSecondImageUri(Uri secondImageUri) {
        this.secondImageUri = secondImageUri;
        secondImageUriLiveData.setValue(secondImageUri);
    }

    public void setFirstImageUri(Uri firstImageUri) {
        this.firstImageUri = firstImageUri;
        firstImageUriLiveData.setValue(firstImageUri);
    }

    public void setExerciseMechanic(String exerciseMechanic) {
        this.exerciseMechanic = exerciseMechanic;
    }

    public void setExerciseSelectedMuscle(String newExerciseSelectedMuscle) {
        this.exerciseSelectedMuscle = newExerciseSelectedMuscle;
    }

    public LiveData<Boolean> getRepeatedNameTransformation() {
        return repeatedNameTransformation;
    }

    public LiveData<NetworkState> getNetworkStateLiveData() {
        return networkStateLiveData;
    }

    public LiveData<Event<ExerciseFieldIssue>> getMissingFieldLiveData() {
        return missingFieldLiveData;
    }


    public LiveData<Integer> getUploadProgressLiveData() {
        return uploadProgressLiveData;
    }


    /**
     * check if there is an exercise with same name in DB exists
     */
    public void checkRepeatedName(final String name) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(EXERCISES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsBig : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds : dsBig.getChildren()) {
                        if (ds.hasChild(NAME) && ds.child(NAME).getValue().toString().toLowerCase().trim().equals(name.toLowerCase().trim())) {
                            repeatedNameLiveData.setValue(true);
                            networkStateLiveData.setValue(NetworkState.STOP_LOADING);
                            return;
                        }
                    }
                }
                repeatedNameLiveData.setValue(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void uploadExercise(final Exercise exercise) {
        Log.d(TAG, "uploadExercise: "+exercise.toString());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        String exerciseKey = reference.child(EXERCISES).push().getKey();
        exercise.setId(exerciseKey);
        reference.child(EXERCISES).child(exerciseKey).setValue(exercise).addOnSuccessListener(aVoid ->
                networkStateLiveData.setValue(NetworkState.SUCCESS))
                .addOnFailureListener(e -> {
                    networkStateLiveData.setValue(NetworkState.ERROR);
                });

    }


    public void uploadFirstExercisePhoto() {
        // Create a storage reference from our app
        final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference firstImageRef = storageRef.child(EXERCISE_IMAGES).child(System.currentTimeMillis()+"");

        firstImageRef.putFile(firstImageUri).addOnProgressListener(taskSnapshot -> {
                    //returns half of the real progress since this is only one image of two
                    double progress = (50.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    uploadProgressLiveData.setValue((int) progress);
                }
        ).addOnSuccessListener(taskSnapshot -> {
            //first image uploaded successfully
            firstImageRef.getDownloadUrl().addOnSuccessListener(url -> {
                Log.d(TAG, "getDownloadUrl: " + url);
                firstImageDownloadUrlLiveData.setValue(url);
            }).addOnFailureListener(e -> {
                Log.d(TAG, "getDownloadUrl: " + e.getMessage());
            });
        }).addOnFailureListener(e -> {
            //upload first image failed
            Log.d(TAG, "uploadFirstExercisePhoto: " + e.getMessage());
            deleteExerciseFromDb();
        });
    }

    private void deleteExerciseFromDb() {
        //todo delete exercise
    }

    private void uploadSecondExercisePhoto() {

        final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference imagesRef = storageRef.child(EXERCISE_IMAGES).child(System.currentTimeMillis()+"");

        imagesRef.putFile(secondImageUri).addOnProgressListener(taskSnapshot1 -> {
            double progress = (50.0 + (50.0 * taskSnapshot1.getBytesTransferred()) / taskSnapshot1.getTotalByteCount());
            uploadProgressLiveData.setValue((int) progress);
        }).addOnFailureListener(e -> {
            //uploading second image failed so no need to keep first uploaded image
            deleteExerciseFromDb();
            //todo delete first image
        }).addOnSuccessListener(aVoid -> {
            imagesRef.getDownloadUrl().addOnSuccessListener(url -> {
                //second image uploaded successfully
                secondImageDownloadUriLiveData.setValue(url);
                Log.d(TAG, "getDownloadUrl2: "+url);
            }).addOnFailureListener(e -> {
                Log.d(TAG, "uploadSecondExercisePhoto: " + e.getMessage());
            });
        });
    }

    public void saveExercise(String exerciseName, String exerciseExecution, String additionalNotes) {
        if (exerciseName.trim().length() < 6) {
            missingFieldLiveData.setValue(new Event<>(ExerciseFieldIssue.NAME));
        } else if (exerciseExecution.trim().length() < 30) {
            missingFieldLiveData.setValue(new Event<>(ExerciseFieldIssue.EXECUTION));
        } else if (exerciseMechanic == null || exerciseMechanic.trim().isEmpty()) {
            missingFieldLiveData.setValue(new Event<>(ExerciseFieldIssue.MECHANIC));
        } else if (exerciseSelectedMuscle == null || exerciseSelectedMuscle.trim().isEmpty()) {
            missingFieldLiveData.setValue(new Event<>(ExerciseFieldIssue.TARGET_MUSCLE));
        } else if (firstImageUri == null || secondImageUri == null) {
            missingFieldLiveData.setValue(new Event<>(ExerciseFieldIssue.PHOTO));
        } else {
            networkStateLiveData.setValue(NetworkState.LOADING);

            exercise = new Exercise(exerciseName, exerciseSelectedMuscle, exerciseExecution
                    , exerciseMechanic, formatUriAsTimeStampedString(firstImageUri),
                    formatUriAsTimeStampedString(secondImageUri));
            exercise.setCreatorId(AuthUtils.getLoggedUserId(app));
            exercise.setDate(String.valueOf(System.currentTimeMillis()));
            if (!additionalNotes.trim().isEmpty()) {
                exercise.setAdditional_notes(additionalNotes);
            }
            checkRepeatedName(exerciseName);
        }
    }
}