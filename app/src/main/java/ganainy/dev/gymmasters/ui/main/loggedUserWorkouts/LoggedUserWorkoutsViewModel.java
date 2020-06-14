package ganainy.dev.gymmasters.ui.main.loggedUserWorkouts;

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

import ganainy.dev.gymmasters.models.app_models.Workout;
import ganainy.dev.gymmasters.utils.FirebaseUtils;
import ganainy.dev.gymmasters.utils.NetworkState;

public class LoggedUserWorkoutsViewModel extends ViewModel {

    private List<Workout> workoutList = new ArrayList<>();
    private MutableLiveData<List<Workout>> workoutListLiveData = new MutableLiveData<>();
    private MutableLiveData<NetworkState> networkStateLiveData =new MutableLiveData<>();


    public LiveData<NetworkState> getNetworkStateLiveData() {
        return networkStateLiveData;
    }


    public LiveData<List<Workout>> getWorkoutListLiveData() {
        return workoutListLiveData;
    }



    public void downloadLoggedUserWorkouts(String loggedUserId) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("workout").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                workoutList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.hasChild("creatorId") &&
                            ds.child("creatorId").getValue().equals(loggedUserId)) {
                        Workout workout = FirebaseUtils.getWorkoutFromSnapshot(ds);
                        workoutList.add(workout);
                    }
                }
                if (workoutList.isEmpty()){
                    networkStateLiveData.setValue(NetworkState.EMPTY);
                }else {
                    workoutListLiveData.setValue(workoutList);
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