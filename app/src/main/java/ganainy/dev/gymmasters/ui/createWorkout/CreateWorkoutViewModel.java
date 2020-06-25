package ganainy.dev.gymmasters.ui.createWorkout;

import android.app.Application;
import android.net.Uri;
import android.text.Editable;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.Workout;
import ganainy.dev.gymmasters.utils.AuthUtils;
import ganainy.dev.gymmasters.utils.Event;
import ganainy.dev.gymmasters.utils.MiscellaneousUtils;
import ganainy.dev.gymmasters.utils.NetworkState;

import static ganainy.dev.gymmasters.ui.exercise.ExercisesViewModel.EXERCISES;

public class CreateWorkoutViewModel extends ViewModel {

    public static final String WORKOUT_IMAGES = "workoutImages/";
    public static final String TAG = "CreateWorkoutViewModel";
    public static final String WORKOUT = "workout";

    List<Exercise> downloadedExerciseList = new ArrayList<>();
    List<Exercise> filteredExerciseList = new ArrayList<>();

    public MutableLiveData<List<Exercise>> getDownloadedExerciseListLiveData() {
        return downloadedExerciseListLiveData;
    }

    private MutableLiveData<List<Exercise>> downloadedExerciseListLiveData = new MutableLiveData<>();

    public void addExerciseToWorkout(Exercise exercise) {
        for (Exercise exerciseAtIndex : downloadedExerciseList) {
            if (exerciseAtIndex.getName().equals(exercise.getName())) {
                exerciseAtIndex.setIsAddedToWorkout(true);
            }
        }
        downloadedExerciseListLiveData.setValue(downloadedExerciseList);
    }

    public void removeExerciseFromWorkout(Exercise exercise) {
        for (Exercise exerciseAtIndex : downloadedExerciseList) {
            if (exerciseAtIndex.getName().equals(exercise.getName())) {
                exerciseAtIndex.setIsAddedToWorkout(false);
            }
        }
        downloadedExerciseListLiveData.setValue(downloadedExerciseList);
    }

    private Uri imageUri;

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    private String workoutName;

    public void setWorkoutDuration(String workoutDuration) {
        this.workoutDuration = workoutDuration;
    }

    private String workoutDuration;

    public void setWorkoutLevel(String workoutLevel) {
        this.workoutLevel = workoutLevel;
    }

    private String workoutLevel;
    Application app;

    public CreateWorkoutViewModel(Application app) {
        this.app = app;
        downloadAllExercises();
    }

    public MutableLiveData<Integer> getUploadProgressLiveData() {
        return uploadProgressLiveData;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
        imageUriLiveData.setValue(imageUri);
    }

    private MutableLiveData<Integer> uploadProgressLiveData = new MutableLiveData<>();

    public MutableLiveData<Event<WorkoutFieldIssue>> getFieldIssueLiveData() {
        return fieldIssueLiveData;
    }

    private MutableLiveData<Event<WorkoutFieldIssue>> fieldIssueLiveData = new MutableLiveData<>();

    public MutableLiveData<Uri> getImageUriLiveData() {
        return imageUriLiveData;
    }

    private MutableLiveData<Uri> imageUriLiveData = new MutableLiveData<>();

    public MutableLiveData<NetworkState> getNetworkStateLiveData() {
        return networkStateLiveData;
    }

    private MutableLiveData<NetworkState> networkStateLiveData = new MutableLiveData<>();

    public MutableLiveData<List<Exercise>> getExerciseListLiveData() {
        return exerciseListLiveData;
    }

    private MutableLiveData<List<Exercise>> exerciseListLiveData = new MutableLiveData<>();


    void uploadWorkoutImage() {

        networkStateLiveData.setValue(NetworkState.LOADING);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String photoPath = WORKOUT_IMAGES + MiscellaneousUtils.formatUriAsTimeStampedString(imageUri);
        final StorageReference imagesRef = storageRef.child(photoPath);
        imagesRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            uploadWorkout(photoPath);
        }).addOnFailureListener(e -> {
            networkStateLiveData.setValue(NetworkState.ERROR);

        }).addOnProgressListener(taskSnapshot -> {
            //calculating progress percentage
            final double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            uploadProgressLiveData.setValue((int) progress);
        });
    }


    private void uploadWorkout(String photoPath) {
        //todo add workout id like we did with exercise
        DatabaseReference workoutRef = FirebaseDatabase.getInstance().getReference(WORKOUT);

        Workout workout = new Workout();
        workout.setName(workoutName);
        workout.setDuration(workoutDuration);
        workout.setLevel(workoutLevel);
        workout.setPhotoLink(photoPath);
        workout.setCreatorId(AuthUtils.getLoggedUserId(app));
        /*save user id and date with workout*/
        String id = workoutRef.push().getKey();
        workout.setId(id);
        workout.setDate(String.valueOf(System.currentTimeMillis()));

        List<Exercise> workoutExercisesList = getWorkoutExercisesList();
        workout.setExercisesNumber(String.valueOf(workoutExercisesList.size()));
        workout.setWorkoutExerciseList(workoutExercisesList);

        workoutRef.child(id).setValue(workout).addOnSuccessListener(aVoid -> {
            networkStateLiveData.setValue(NetworkState.SUCCESS);
        }).addOnFailureListener(e -> {
            networkStateLiveData.setValue(NetworkState.ERROR);
        });
    }

    private List<Exercise> getWorkoutExercisesList() {
        List<Exercise> workoutExercisesList=new ArrayList<>();
        for (Exercise exercise:downloadedExerciseList){
            if (exercise.getIsAddedToWorkout()!=null && exercise.getIsAddedToWorkout()){
                workoutExercisesList.add(exercise);
            }
        }
        return workoutExercisesList;
    }


    private void downloadAllExercises() {
        FirebaseDatabase.getInstance().getReference(EXERCISES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot exerciseSnapshot : dataSnapshot.getChildren()) {
                        Exercise exercise =exerciseSnapshot.getValue(Exercise.class);
                        downloadedExerciseList.add(exercise);
                }
                exerciseListLiveData.setValue(downloadedExerciseList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public boolean validateInputs(String workoutName, String workoutDuration) {
        if (workoutName.length() < 6) {
            fieldIssueLiveData.setValue(new Event<>(WorkoutFieldIssue.NAME));
            return false;
        } else if (!checkInDurationRange(workoutDuration)) {
            fieldIssueLiveData.setValue(new Event<>(WorkoutFieldIssue.DURATION));
            return false;
        } else if (imageUri == null) {
            fieldIssueLiveData.setValue(new Event<>(WorkoutFieldIssue.IMAGE));
            return false;
        } else if (workoutLevel.trim().isEmpty()) {
            fieldIssueLiveData.setValue(new Event<>(WorkoutFieldIssue.LEVEL));
            return false;
        } else if (downloadedExerciseList == null || downloadedExerciseList.size() == 0) {
            fieldIssueLiveData.setValue(new Event<>(WorkoutFieldIssue.EMPTY_EXERCISES));
            return false;
        } else {

            return true;
        }
    }

    private boolean checkInDurationRange(String text) {
        return Integer.parseInt(text) > 0 && Integer.parseInt(text) <= 120;
    }


    void filterExercises(Editable editable) {
        filteredExerciseList.clear();
        for (Exercise exercise : downloadedExerciseList) {
            if (exercise.getName().contains(editable.toString())) {
                filteredExerciseList.add(exercise);
            }
        }
        downloadedExerciseListLiveData.setValue(filteredExerciseList);
    }


}
