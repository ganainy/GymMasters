package ganainy.dev.gymmasters.ui.posts;

import android.app.Application;

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
import ganainy.dev.gymmasters.models.app_models.Post;
import ganainy.dev.gymmasters.models.app_models.Workout;
import ganainy.dev.gymmasters.utils.AuthUtils;
import ganainy.dev.gymmasters.utils.FirebaseUtils;
import ganainy.dev.gymmasters.utils.NetworkState;

import static ganainy.dev.gymmasters.ui.exercise.ExercisesViewModel.EXERCISES;

public class PostsViewModel extends ViewModel {

    public static final String USERS = "users";
    public static final String FOLLOWING_UID = "followingUID";
    public static final String CREATOR_ID = "creatorId";
    public static final String WORKOUT = "workout";

    Application app;
    private List<String> mFollowingIdList = new ArrayList<>();
    private List<Post> postList = new ArrayList<>();

    public LiveData<NetworkState> getNetworkStateLiveData() {
        return networkStateLiveData;
    }

    private MutableLiveData<NetworkState> networkStateLiveData=new MutableLiveData<>();

    public MutableLiveData<List<Post>> getPostListLiveData() {
        return postListLiveData;
    }

    private MutableLiveData< List<Post>> postListLiveData=new MutableLiveData<>();

    public PostsViewModel(Application app) {
        this.app = app;
    }

    /**get ids of user that current logged in user is following*/
    void getFollowingUid() {
        networkStateLiveData.setValue(NetworkState.LOADING);

        if (!mFollowingIdList.isEmpty())return;

        final DatabaseReference users = FirebaseDatabase.getInstance().getReference().child(USERS).child(AuthUtils.getLoggedUserId(app));
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(FOLLOWING_UID)) {
                    users.child(FOLLOWING_UID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                mFollowingIdList.add(ds.getValue().toString());
                            }
                            getExercises();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            networkStateLiveData.setValue(NetworkState.ERROR);
                        }
                    });
                } else {
                    //logged in user doesn't follow anyone
                    networkStateLiveData.setValue(NetworkState.EMPTY);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getExercises() {

        DatabaseReference exerciseNode = FirebaseDatabase.getInstance().getReference(EXERCISES);
        exerciseNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsBig : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds : dsBig.getChildren()) {
                        for (int i = 0; i < mFollowingIdList.size(); i++) {
                            if (ds.child(CREATOR_ID).getValue().equals(mFollowingIdList.get(i))) {
                                Exercise exercise = FirebaseUtils.getExerciseFromSnapshot(ds);
                                postList.add(new Post(exercise, 0, Long.valueOf(exercise.getDate())));
                            }
                        }
                    }
                }
                getWorkouts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                networkStateLiveData.setValue(NetworkState.ERROR);
            }
        });


    }

    void getWorkouts() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(WORKOUT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //only show in main list the workouts that admin added
                    for (int i = 0; i < mFollowingIdList.size(); i++) {
                        if (ds.child(CREATOR_ID).getValue().equals(mFollowingIdList.get(i))) {
                            Workout workout =FirebaseUtils.getWorkoutFromSnapshot(ds);
                            postList.add(new Post(workout, 1, Long.valueOf(workout.getDate())));
                        }

                    }
                }

                networkStateLiveData.setValue(NetworkState.SUCCESS);
                postListLiveData.setValue(postList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                networkStateLiveData.setValue(NetworkState.ERROR);
            }
        });

    }


}
