package ganainy.dev.gymmasters.ui.exercise;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ganainy.dev.gymmasters.models.app_models.Exercise;

import static ganainy.dev.gymmasters.ui.main.exercises.MainFragmentExcercies.SHOWALL;

public class ExercisesViewModel extends ViewModel {
    private static final String TAG = "ExerciseActivityViewMod";
    public static final String EXERCISES = "exercises";
    public static final String CREATOR_ID = "creatorId";

    private ArrayList<Exercise> loggedUserExercisesArrayList= new ArrayList<>();
    private List<Exercise> selectedMuscleExerciseArrayList = new ArrayList<>();

    private MutableLiveData<List<Exercise>> exerciseListLiveData =new MutableLiveData<>();
    private MutableLiveData<Boolean> loadingStateLiveData =new MutableLiveData<>();
    private MutableLiveData<Boolean> emptyStateLiveData =new MutableLiveData<>();


    public LiveData<Boolean> getEmptyStateLiveData() {
        return emptyStateLiveData;
    }

    public LiveData<Boolean> getLoadingStateLiveData() {
        return loadingStateLiveData;
    }

    public LiveData<List<Exercise>> getExerciseListLiveData() {
        return exerciseListLiveData;
    }

    /**load all/certain muscle exercises from firebase database*/
    public void getSelectedMuscleExercises(String muscle) {
        loadingStateLiveData.setValue(true);

        final DatabaseReference exercisesRef = FirebaseDatabase.getInstance().getReference(EXERCISES);

        if (!muscle.equals(SHOWALL)) {
            DatabaseReference selectedMuscleRef =  exercisesRef.child(muscle);
            selectedMuscleExerciseArrayList.clear();
            selectedMuscleRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds:dataSnapshot.getChildren()) {
                        Exercise exerciseFromSnapshot = getExerciseFromSnapshot(ds);
                        selectedMuscleExerciseArrayList.add(exerciseFromSnapshot);
                    }
                    loadingStateLiveData.setValue(false);
                    if (selectedMuscleExerciseArrayList.size()==0)
                        emptyStateLiveData.setValue(true);
                    else {
                        exerciseListLiveData.setValue(selectedMuscleExerciseArrayList);
                        emptyStateLiveData.setValue(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }else {
            loadAllExercises();
        }
    }

    /**extract exercise model from snapshot*/
    private Exercise getExerciseFromSnapshot(DataSnapshot snapshot) {
            Exercise exercise = new Exercise();
            if (snapshot.hasChild("name"))
            exercise.setName(snapshot.child("name").getValue().toString());
            if (snapshot.hasChild("execution"))
            exercise.setExecution(snapshot.child("execution").getValue().toString());
            if (snapshot.hasChild("additional_notes"))
                exercise.setAdditional_notes(snapshot.child("additional_notes").getValue().toString());
            if (snapshot.hasChild("bodyPart"))
            exercise.setBodyPart(snapshot.child("bodyPart").getValue().toString());
            if (snapshot.hasChild("mechanism"))
            exercise.setMechanism(snapshot.child("mechanism").getValue().toString());
            if (snapshot.hasChild("previewPhoto1"))
            exercise.setPreviewPhoto1(snapshot.child("previewPhoto1").getValue().toString());
            if (snapshot.hasChild("previewPhoto2"))
            exercise.setPreviewPhoto2(snapshot.child("previewPhoto2").getValue().toString());
           return exercise;
    }

    private void loadAllExercises() {
        loadingStateLiveData.setValue(true);
        final DatabaseReference exercisesNode = FirebaseDatabase.getInstance().getReference(EXERCISES);

        exercisesNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                selectedMuscleExerciseArrayList.clear();
                for (DataSnapshot dsBig : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds : dsBig.getChildren()) {
                        Exercise exerciseFromSnapshot = getExerciseFromSnapshot(ds);
                        selectedMuscleExerciseArrayList.add(exerciseFromSnapshot);
                        }
                }
                loadingStateLiveData.setValue(false);
                if (selectedMuscleExerciseArrayList.size()==0)
                    emptyStateLiveData.setValue(true);
                else {
                    exerciseListLiveData.setValue(selectedMuscleExerciseArrayList);
                    emptyStateLiveData.setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    public void downloadLoggedUserExercises(String loggedInUserId) {
        loadingStateLiveData.setValue(true);
        final DatabaseReference exerciseNode = FirebaseDatabase.getInstance().getReference(EXERCISES);
        exerciseNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loggedUserExercisesArrayList.clear();
                for (DataSnapshot dsBig : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds : dsBig.getChildren()) {
                        if (ds.hasChild(CREATOR_ID) && ds.child(CREATOR_ID).getValue().equals(loggedInUserId)) {
                            Exercise exerciseFromSnapshot = getExerciseFromSnapshot(ds);
                            loggedUserExercisesArrayList.add(exerciseFromSnapshot);
                        }
                    }
                }
                loadingStateLiveData.setValue(false);
                if (loggedUserExercisesArrayList.size()==0)
                    emptyStateLiveData.setValue(true);
                else {
                    exerciseListLiveData.setValue(loggedUserExercisesArrayList);
                    emptyStateLiveData.setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError);
            }
        });
    }


}
