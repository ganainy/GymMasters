package ganainy.dev.gymmasters.ui.muscle;

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
import ganainy.dev.gymmasters.utils.NetworkState;

import static ganainy.dev.gymmasters.ui.main.exercisesCategories.ExercisesCategoriesFragment.SHOWALL;
import static ganainy.dev.gymmasters.utils.Constants.EXERCISES;

public class MuscleViewModel extends ViewModel {
    private static final String TAG = "MuscleViewModel";

    public List<Exercise> getSelectedMuscleExerciseList() {
        return selectedMuscleExerciseList;
    }

    private List<Exercise> selectedMuscleExerciseList = new ArrayList<>();
    private MutableLiveData<List<Exercise>> exerciseListLiveData =new MutableLiveData<>();
    private MutableLiveData<NetworkState> networkStateLiveData =new MutableLiveData<>();

    public LiveData<NetworkState> getNetworkStateLiveData() {
        return networkStateLiveData;
    }
    public LiveData<List<Exercise>> getExerciseListLiveData() {
        return exerciseListLiveData;
    }

    /**load all/certain muscle exercises from firebase database*/
    public void getSelectedMuscleExercises(String muscle) {
        networkStateLiveData.setValue(NetworkState.LOADING);

        final DatabaseReference exercisesRef = FirebaseDatabase.getInstance().getReference(EXERCISES);

        if (!muscle.equals(SHOWALL)) {

            selectedMuscleExerciseList.clear();
            exercisesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds:dataSnapshot.getChildren()) {
                        Exercise exercise = ds.getValue(Exercise.class);
                        if (exercise.getBodyPart().equals(muscle)){
                            selectedMuscleExerciseList.add(exercise);
                        }
                    }
                    if (selectedMuscleExerciseList.size()==0)
                        networkStateLiveData.setValue(NetworkState.EMPTY);
                    else {
                        exerciseListLiveData.setValue(selectedMuscleExerciseList);
                        networkStateLiveData.setValue(NetworkState.SUCCESS);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    networkStateLiveData.setValue(NetworkState.ERROR);
                }
            });
        }else {
            loadAllExercises();
        }
    }


    private void loadAllExercises() {
        networkStateLiveData.setValue(NetworkState.LOADING);
        final DatabaseReference exercisesNode = FirebaseDatabase.getInstance().getReference(EXERCISES);

        exercisesNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                selectedMuscleExerciseList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Exercise exerciseFromSnapshot = ds.getValue(Exercise.class);
                        selectedMuscleExerciseList.add(exerciseFromSnapshot);

                }
                if (selectedMuscleExerciseList.size()==0)
                    networkStateLiveData.setValue(NetworkState.EMPTY);
                else {
                    exerciseListLiveData.setValue(selectedMuscleExerciseList);
                    networkStateLiveData.setValue(NetworkState.SUCCESS);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                networkStateLiveData.setValue(NetworkState.ERROR);
            }
        });

    }






}
