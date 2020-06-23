package ganainy.dev.gymmasters.ui.posts;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.Post;
import ganainy.dev.gymmasters.models.app_models.Workout;
import ganainy.dev.gymmasters.utils.AuthUtils;
import ganainy.dev.gymmasters.utils.FirebaseUtils;
import ganainy.dev.gymmasters.utils.NetworkState;

import static ganainy.dev.gymmasters.ui.exercise.ExercisesViewModel.EXERCISES;
import static ganainy.dev.gymmasters.utils.Constants.SOCIAL;

public class PostsViewModel extends ViewModel {

    public static final String USERS = "users";
    public static final String FOLLOWING_UID = "followingUID";
    public static final String CREATOR_ID = "creatorId";
    public static final String WORKOUT = "workout";
    public static final String NAME = "name";
    public static final String PHOTO = "photo";
    public static final String TAG = "PostsViewModel";

    Application app;
    private List<String> mFollowingIdList = new ArrayList<>();

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }

    private List<Post> postList = new ArrayList<>();

    public LiveData<NetworkState> getNetworkStateLiveData() {
        return networkStateLiveData;
    }

    private MutableLiveData<NetworkState> networkStateLiveData=new MutableLiveData<>();

    public MutableLiveData<List<Post>> getPostListLiveData() {
        return postListLiveData;
    }

    private MutableLiveData<List<Post>> postListLiveData=new MutableLiveData<>();

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
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        for (int i = 0; i < mFollowingIdList.size(); i++) {
                            if (ds.child(CREATOR_ID).getValue().equals(mFollowingIdList.get(i))) {
                                Exercise exercise = ds.getValue(Exercise.class);
                                if (exercise.getLikerIdList()==null){
                                    //post has no likes
                                    postList.add(new Post(exercise, 0, Long.valueOf(exercise.getDate()), false));
                                }else if (exercise.getLikerIdList().contains(AuthUtils.getLoggedUserId(app))){
                                    //logged user liked post
                                    postList.add(new Post(exercise, 0, Long.valueOf(exercise.getDate()), true));
                                }else if (!exercise.getLikerIdList().contains(AuthUtils.getLoggedUserId(app))){
                                    //logged user DIDN'T liked post
                                    postList.add(new Post(exercise, 0, Long.valueOf(exercise.getDate()), false));
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
                            postList.add(new Post(workout, 1, Long.valueOf(workout.getDate()),false));
                        }

                    }
                }

                getNamesAndImageUrlsPostsOwners();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                networkStateLiveData.setValue(NetworkState.ERROR);
            }
        });

    }

    private void getNamesAndImageUrlsPostsOwners() {


            FirebaseDatabase.getInstance().getReference(USERS).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        for (Post post:postList){
                            if (post.getExercise()!=null && ds.getKey().equals(post.getExercise().getCreatorId())) {
                            post.getExercise().setCreatorName (ds.child(NAME).getValue().toString());
                            if (ds.hasChild(PHOTO))
                            post.getExercise().setCreatorImageUrl(ds.child(PHOTO).getValue().toString());
                        }else if (post.getWorkout()!=null && ds.getKey().equals(post.getWorkout().getCreatorId())){
                            post.getWorkout().setCreatorName (ds.child(NAME).getValue().toString());
                            if (ds.hasChild(PHOTO))
                            post.getWorkout().setCreatorImageUrl(ds.child(PHOTO).getValue().toString());
                        }
                    }
                    }


                    networkStateLiveData.setValue(NetworkState.SUCCESS);
                    postListLiveData.setValue(postList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: getNamesAndImageUrlsPostsOwners");
                }
            });

        //todo add like count and comment count node to exercise and show in adapter
    }



    public void likePost(String postId, Integer adapterPosition) {

  /*      reference.child(SOCIAL).child(exerciseKey).setValue(0);//add node in social with this exercise id
        //which will be used later for counting likes/comments*/


        FirebaseDatabase.getInstance().getReference(EXERCISES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot exerciseIdSnap:snapshot.getChildren()) {
                    if (exerciseIdSnap.getKey().equals(postId)){
                        Exercise exercise = exerciseIdSnap.getValue(Exercise.class);

                        if (exercise.getLikerIdList()!=null){
                            if (exercise.getLikerIdList().contains(AuthUtils.getLoggedUserId(app))){
                                //logged user already liked this post
                                exercise.getLikerIdList().remove(AuthUtils.getLoggedUserId(app));
                            }else{
                                //logged user didn't like this post
                                exercise.getLikerIdList().add(AuthUtils.getLoggedUserId(app));
                            }
                        }else {
                            //this exercise has no likes
                            ArrayList<String> likerIdList = new ArrayList<>();
                            likerIdList.add(AuthUtils.getLoggedUserId(app));
                            exercise.setLikerIdList(likerIdList);
                        }

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("likerIdList", exercise.getLikerIdList());
                        updates.put("likeCount", exercise.getLikerIdList().size());

                        FirebaseDatabase.getInstance().getReference()
                                .child(EXERCISES)
                                .child(postId)
                                .updateChildren(updates).addOnFailureListener(e -> {

                        });

                    break;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }
}
