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

import java.nio.file.ClosedDirectoryStreamException;

import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.utils.AuthUtils;
import ganainy.dev.gymmasters.utils.Event;
import ganainy.dev.gymmasters.utils.NetworkState;

import static ganainy.dev.gymmasters.ui.exercise.ExercisesViewModel.EXERCISES;
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
                uploadExercise(exercise);
            return isRepeatedName;
        });

        uploadExerciseTransformation=Transformations.map(isExerciseUploadSuccessfulLiveData,isExerciseUploadSuccessfully->{
            Log.d(TAG, "CreateExerciseViewModel: uploadExerciseTransformation");
            if (isExerciseUploadSuccessfully)
            uploadFirstExercisePhoto(firstImageUri);
            return isExerciseUploadSuccessfully;
        });

        firstImageUploadedTransformation =Transformations.map(firstImageUploadedLiveData, firstImageUploaded->{
            Log.d(TAG, "CreateExerciseViewModel: firstImageUploadedTransformation");
           if (firstImageUploaded)
               uploadSecondExercisePhoto(firstImageUri,secondImageUri);
            return firstImageUri;
        });
    }


    private LiveData<Boolean> repeatedNameTransformation;
    private LiveData<Boolean> uploadExerciseTransformation;
    private MutableLiveData<Boolean> repeatedNameLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isExerciseUploadSuccessfulLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> uploadProgressLiveData = new MutableLiveData<>();
    private MutableLiveData<Event<MissingField>> missingFieldLiveData = new MutableLiveData<>();
    private MutableLiveData<NetworkState> networkStateLiveData = new MutableLiveData<>();
    private MutableLiveData<Uri> firstImageUriLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> firstImageUploadedLiveData = new MutableLiveData<>();
    private MutableLiveData<Uri> secondImageUriLiveData = new MutableLiveData<>();
    private LiveData<Uri> firstImageUploadedTransformation;

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

    public LiveData<Boolean> getUploadExerciseTransformation() {
        return uploadExerciseTransformation;
    }

    public LiveData<NetworkState> getNetworkStateLiveData() {
        return networkStateLiveData;
    }

    public LiveData<Event<MissingField>> getMissingFieldLiveData() {
        return missingFieldLiveData;
    }


    public LiveData<Integer> getUploadProgressLiveData() {
        return uploadProgressLiveData;
    }



    /**check if there is an exercise with same name in DB exists*/
    public void checkRepeatedName(final String name) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(EXERCISES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsBig : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds : dsBig.getChildren()) {
                        if (ds.hasChild(NAME) && ds.child(NAME).getValue().toString().toLowerCase().trim().equals(name.toLowerCase().trim())) {
                            repeatedNameLiveData.setValue(true);
                            networkStateLiveData.setValue(NetworkState.SUCCESS);
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

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Task<Void> exercises = reference.child(EXERCISES).child(exercise.getBodyPart().toLowerCase()).push().setValue(exercise);
        exercises.addOnSuccessListener(aVoid ->
                isExerciseUploadSuccessfulLiveData.setValue(true))
                .addOnFailureListener(e -> {
                    isExerciseUploadSuccessfulLiveData.setValue(false);
                });
    }


    public void uploadFirstExercisePhoto(Uri firstPhotoUri) {
        Log.d(TAG, "uploadFirstExercisePhoto: ");
        // Create a storage reference from our app
        final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference firstImageRef = storageRef.child(EXERCISE_IMAGES + formatUriAsTimeStampedString(firstPhotoUri));

        firstImageRef.putFile(firstPhotoUri).addOnProgressListener(taskSnapshot -> {
                    //returns half of the real progress since this is only one image of two
                    double progress = (50.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    uploadProgressLiveData.setValue((int) progress);
                }
        ).addOnSuccessListener(taskSnapshot -> {
            //first image uploaded successfully
            firstImageUploadedLiveData.setValue(true);
        }).addOnFailureListener(e -> {
            //upload first image failed
            Log.d(TAG, "uploadFirstExercisePhoto: "+e.getMessage());
            deleteExerciseFromDb();
        });
    }

    private void deleteExerciseFromDb() {
        //todo delete exercise
    }

    private void uploadSecondExercisePhoto(Uri firstPhotoUri, Uri secondImageUri) {

        final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference firstImageRef = storageRef.child(EXERCISE_IMAGES + formatUriAsTimeStampedString(firstPhotoUri));

        final StorageReference secondImageRef = storageRef.child(EXERCISE_IMAGES + formatUriAsTimeStampedString(secondImageUri));
        secondImageRef.putFile(secondImageUri).addOnProgressListener(taskSnapshot1 -> {
            double progress = (50.0 + (50.0 * taskSnapshot1.getBytesTransferred()) / taskSnapshot1.getTotalByteCount());
            uploadProgressLiveData.setValue((int) progress);
        }).addOnFailureListener(e -> {
            //uploading second image failed so no need to keep first uploaded image
            deleteExerciseFromDb();
            firstImageRef.delete();
        }).addOnSuccessListener(aVoid -> {
            //second image uploaded successfully
            networkStateLiveData.setValue(NetworkState.SUCCESS);
        });
    }

    public void saveExercise(String exerciseName, String exerciseExecution, String additionalNotes) {
        if (exerciseName.trim().length() < 6) {
            missingFieldLiveData.setValue(new Event<>(MissingField.NAME));
        } else if (exerciseExecution.trim().length() < 30) {
            missingFieldLiveData.setValue(new Event<>(MissingField.EXECUTION));
        } else if (exerciseMechanic==null || exerciseMechanic.trim().isEmpty()) {
            missingFieldLiveData.setValue(new Event<>(MissingField.MECHANIC));
        } else if (exerciseSelectedMuscle==null ||exerciseSelectedMuscle.trim().isEmpty()) {
            missingFieldLiveData.setValue(new Event<>(MissingField.TARGET_MUSCLE));
        } else if (firstImageUri == null || secondImageUri == null) {
            missingFieldLiveData.setValue(new Event<>(MissingField.PHOTO));
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