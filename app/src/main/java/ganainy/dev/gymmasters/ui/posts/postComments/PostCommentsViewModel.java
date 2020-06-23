package ganainy.dev.gymmasters.ui.posts.postComments;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ganainy.dev.gymmasters.models.app_models.Comment;
import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.Post;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.utils.AuthUtils;

public class PostCommentsViewModel extends AndroidViewModel {

    public static final String USERS = "users";
    public static final String LIKER_ID_LIST = "likerIdList";
    public static final String LIKE_COUNT = "likeCount";

    private List<Pair<Comment, User>> userCommentPairList = new ArrayList<>();

    public LiveData<List<Pair<Comment, User>>> getUserCommentPairListLiveData() {
        return userCommentPairListLiveData;
    }

    private MutableLiveData<List<Pair<Comment, User>>> userCommentPairListLiveData = new MutableLiveData<>();

    public LiveData<List<String>> getLikerIdListLiveData() {
        return likerIdListLiveData;
    }

    private MutableLiveData<List<String>> likerIdListLiveData = new MutableLiveData<>();

    public static final String COMMENT_LIST = "commentList";
    public static final String COMMENT_COUNT = "commentCount";
    public static final String TAG = "PostCommentsViewModel";

    private Post mPost;

    public static final String EXERCISES = "exercises";

    public PostCommentsViewModel(@NonNull Application application) {
        super(application);
    }

    public void saveComment(String commentText) {

        if (mPost.getExercise().getCommentList() == null) {
            //post has NO comments
            ArrayList<Comment> commentList = new ArrayList<>();
            commentList.add(new Comment(AuthUtils.getLoggedUserId(getApplication()), System.currentTimeMillis(), commentText));
            mPost.getExercise().setCommentList(commentList);
        } else {
            //post has previous comments
            mPost.getExercise().getCommentList().add(new Comment(AuthUtils.getLoggedUserId(getApplication()), System.currentTimeMillis(), commentText));
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put(COMMENT_LIST, mPost.getExercise().getCommentList());
        updates.put(COMMENT_COUNT, mPost.getExercise().getCommentList().size());

        FirebaseDatabase.getInstance().getReference().child(EXERCISES).child(mPost.getExercise().getId()).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "onDataChange: suc");
                }).addOnFailureListener(e -> {
            Log.d(TAG, "onDataChange: " + e.getMessage());
            //todo handle error
        });


    }

    public Post getPost() {
        return mPost;
    }

    public void setPost(Post mPost) {
        this.mPost = mPost;
        observePostChanges();
    }

    /**
     * this method will trigger when any comment or like changes on post
     */
    private void observePostChanges() {
        FirebaseDatabase.getInstance().getReference().child(EXERCISES).child(mPost.getExercise().getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot exerciseSnapshot) {
                //comments or likes changed
                Exercise exercise = exerciseSnapshot.getValue(Exercise.class);
                if (exercise.getCommentList() == null) {
                    userCommentPairListLiveData.setValue(null);
                } else {
                    getCommenterInformation(exercise.getCommentList());
                }

                likerIdListLiveData.setValue(exercise.getLikerIdList());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCommenterInformation(List<Comment> commentList) {
        userCommentPairList.clear();
        FirebaseDatabase.getInstance().getReference().child(USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (int i = 0; i <= commentList.size() - 1; i++) {
                        User currentUser = userSnapshot.getValue(User.class);
                        if (currentUser.getId().equals(commentList.get(i).getCommenterId())) {
                            User user = userSnapshot.getValue(User.class);
                            userCommentPairList.add(new Pair<>(commentList.get(i), user));
                        }
                    }
                }
                userCommentPairListLiveData.setValue(userCommentPairList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void likePost() {
        if (mPost.getExercise().getLikerIdList() == null) {
            //post has NO likes
            ArrayList<String> likeIdList = new ArrayList<>();
            likeIdList.add(AuthUtils.getLoggedUserId(getApplication()));
            mPost.getExercise().setLikerIdList(likeIdList);
        } else {
            //post has previous likes
            if (mPost.getExercise().getLikerIdList().contains(AuthUtils.getLoggedUserId(getApplication()))){
                //logged user already liked post
                mPost.getExercise().getLikerIdList().remove(AuthUtils.getLoggedUserId(getApplication()));
            }else{
                //logged user didn't like this post
                mPost.getExercise().getLikerIdList().add(AuthUtils.getLoggedUserId(getApplication()));
            }
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put(LIKER_ID_LIST, mPost.getExercise().getLikerIdList());
        updates.put(LIKE_COUNT, mPost.getExercise().getLikerIdList().size());

        FirebaseDatabase.getInstance().getReference().child(EXERCISES).child(mPost.getExercise().getId()).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "onDataChange: suc");
                }).addOnFailureListener(e -> {
            Log.d(TAG, "onDataChange: " + e.getMessage());
            //todo handle error
        });
    }
}