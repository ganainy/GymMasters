package ganainy.dev.gymmasters.ui.main.posts;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.models.app_models.Workout;
import ganainy.dev.gymmasters.utils.AuthUtils;
import ganainy.dev.gymmasters.utils.Event;
import ganainy.dev.gymmasters.utils.NetworkState;

import static ganainy.dev.gymmasters.ui.muscle.MuscleViewModel.EXERCISES;

public class PostsViewModel extends AndroidViewModel {

    public static final String USERS = "users";
    public static final String FOLLOWING_UID = "followingUID";
    public static final String CREATOR_ID = "creatorId";
    public static final String WORKOUT = "workout";
    public static final String NAME = "name";
    public static final String PHOTO = "photo";
    public static final String TAG = "PostsViewModel";
    public static final String LIKER_ID_LIST="likerIdList";
    public static final String LIKE_COUNT="likeCount";

    public void clearFollowingIdList() {
        this.mFollowingIdList.clear();
    }

    private List<String> mFollowingIdList = new ArrayList<>();

    public PostsViewModel(@NonNull Application application) {
        super(application);
    }

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

    public LiveData<Boolean> getLoadingPostCreatorProfileLiveData() {
        return loadingPostCreatorProfileLiveData;
    }

    private MutableLiveData<Boolean> loadingPostCreatorProfileLiveData=new MutableLiveData<>();

    public MutableLiveData<List<Post>> getPostListLiveData() {
        return postListLiveData;
    }

    private MutableLiveData<List<Post>> postListLiveData=new MutableLiveData<>();

    public LiveData<Event<Pair<List<Post>, Integer>>> getUpdatePostLiveData() {
        return updatePostLiveData;
    }

    private MutableLiveData<Event<Pair<List<Post>,Integer>>> updatePostLiveData=new MutableLiveData<>();

    /**get ids of user that current logged in user is following*/
    void getFollowingUid() {
        if (!mFollowingIdList.isEmpty())return;

        networkStateLiveData.setValue(NetworkState.LOADING);

        final DatabaseReference users = FirebaseDatabase.getInstance().getReference().child(USERS).child(AuthUtils.getLoggedUserId(getApplication()));
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
                    //logged in user doesn't follow_black anyone
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
                                }else if (exercise.getLikerIdList().contains(AuthUtils.getLoggedUserId(getApplication()))){
                                    //logged user liked post
                                    postList.add(new Post(exercise, 0, Long.valueOf(exercise.getDate()), true));
                                }else if (!exercise.getLikerIdList().contains(AuthUtils.getLoggedUserId(getApplication()))){
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
                    for (int i = 0; i < mFollowingIdList.size(); i++) {
                        if (ds.child(CREATOR_ID).getValue().equals(mFollowingIdList.get(i))) {
                            Workout workout =ds.getValue(Workout.class);
                            postList.add(new Post(workout, 1, Long.valueOf(workout.getDate()),false));
                        }
                    }
                }
                if (!postList.isEmpty())
                getNamesAndImageUrlsPostsOwners();
                else
                    networkStateLiveData.setValue(NetworkState.EMPTY);
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

    }



    public void likePost(Post post, Integer adapterPosition) {
        if (post.getEntityType()==0){
            //this is exercise
            likeExercise(post,adapterPosition);
        }else if (post.getEntityType()==1){
            //this is workout
           likeWorkout(post,adapterPosition);
        }


    }

    private void likeWorkout(Post post,Integer adapterPosition) {
        if (post.getWorkout().getLikerIdList() == null) {
            //post has NO likes
            ArrayList<String> likeIdList = new ArrayList<>();
            likeIdList.add(AuthUtils.getLoggedUserId(getApplication()));
            post.getWorkout().setLikerIdList(likeIdList);
        } else {
            //post has previous likes
            if (post.getWorkout().getLikerIdList().contains(AuthUtils.getLoggedUserId(getApplication()))){
                //logged user already liked post
                post.getWorkout().getLikerIdList().remove(AuthUtils.getLoggedUserId(getApplication()));
            }else{
                //logged user didn't like this post
                post.getWorkout().getLikerIdList().add(AuthUtils.getLoggedUserId(getApplication()));
            }
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put(LIKER_ID_LIST, post.getWorkout().getLikerIdList());
        updates.put(LIKE_COUNT, post.getWorkout().getLikerIdList().size());

        FirebaseDatabase.getInstance().getReference().child(WORKOUT).child(post.getWorkout().getId()).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    postList.set(adapterPosition,post);
                    updatePostLiveData.setValue(new Event<>(new Pair<>(postList,adapterPosition)));
                }).addOnFailureListener(e -> {
            Log.d(TAG, "onDataChange: " + e.getMessage());
            //todo handle error
        });
    }

    private void likeExercise(Post post, Integer adapterPosition) {

        if (post.getExercise().getLikerIdList() == null) {
            //post has NO likes
            ArrayList<String> likeIdList = new ArrayList<>();
            likeIdList.add(AuthUtils.getLoggedUserId(getApplication()));
            post.getExercise().setLikerIdList(likeIdList);
        } else {
            //post has previous likes
            if (post.getExercise().getLikerIdList().contains(AuthUtils.getLoggedUserId(getApplication()))){
                //logged user already liked post
                post.getExercise().getLikerIdList().remove(AuthUtils.getLoggedUserId(getApplication()));
            }else{
                //logged user didn't like this post
                post.getExercise().getLikerIdList().add(AuthUtils.getLoggedUserId(getApplication()));
            }
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put(LIKER_ID_LIST, post.getExercise().getLikerIdList());
        updates.put(LIKE_COUNT, post.getExercise().getLikerIdList().size());

        FirebaseDatabase.getInstance().getReference().child(EXERCISES).child(post.getExercise().getId()).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    postList.set(adapterPosition,post);
                    updatePostLiveData.setValue(new Event<>(new Pair<>(postList,adapterPosition)));
                }).addOnFailureListener(e -> {
            Log.d(TAG, "onDataChange: " + e.getMessage());
            //todo handle error
        });
    }


    public LiveData<User> getLoggedUser(){
        MutableLiveData<User> loggedUserLiveData=new MutableLiveData<>();
        FirebaseDatabase.getInstance().getReference().child(USERS).child(AuthUtils.getLoggedUserId(getApplication())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                User loggedUser = userSnapshot.getValue(User.class);
                loggedUserLiveData.setValue(loggedUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return loggedUserLiveData;
    }

    public LiveData<User> getUserById(String postCreatorId) {
        loadingPostCreatorProfileLiveData.setValue(true);
        MutableLiveData<User> postCreatorLiveData=new MutableLiveData<>();
        FirebaseDatabase.getInstance().getReference().child(USERS).child(postCreatorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                User postCreator = userSnapshot.getValue(User.class);
                postCreatorLiveData.setValue(postCreator);
                loadingPostCreatorProfileLiveData.setValue(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingPostCreatorProfileLiveData.setValue(false);
            }
        });
        return postCreatorLiveData;
    }
}
