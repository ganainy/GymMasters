package ganainy.dev.gymmasters.ui.userExercises;

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

import static ganainy.dev.gymmasters.ui.exercise.ExercisesViewModel.EXERCISES;
import static ganainy.dev.gymmasters.utils.FirebaseUtils.getExerciseFromSnapshot;

public class UserExercisesViewModel extends ViewModel {
    public static final String CREATOR_ID = "creatorId";

    private MutableLiveData<NetworkState> networkStateLiveData =new MutableLiveData<>();
    private ArrayList<Exercise> loggedUserExercisesArrayList= new ArrayList<>();
    private MutableLiveData<List<Exercise>> exerciseListLiveData =new MutableLiveData<>();

    public LiveData<List<Exercise>> getExerciseListLiveData() {
        return exerciseListLiveData;
    }
    public LiveData<NetworkState> getNetworkStateLiveData() {
        return networkStateLiveData;
    }

    public void downloadLoggedUserExercises(String loggedInUserId) {
        networkStateLiveData.setValue(NetworkState.LOADING);
        final DatabaseReference exerciseNode = FirebaseDatabase.getInstance().getReference(EXERCISES);
        exerciseNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loggedUserExercisesArrayList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.hasChild(CREATOR_ID) && ds.child(CREATOR_ID).getValue().equals(loggedInUserId)) {
                            Exercise exerciseFromSnapshot = getExerciseFromSnapshot(ds);
                            loggedUserExercisesArrayList.add(exerciseFromSnapshot);
                        }
                }

                if (loggedUserExercisesArrayList.size()==0)
                    networkStateLiveData.setValue(NetworkState.EMPTY);
                else {
                    exerciseListLiveData.setValue(loggedUserExercisesArrayList);
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