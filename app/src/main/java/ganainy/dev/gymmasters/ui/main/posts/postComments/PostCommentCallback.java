package ganainy.dev.gymmasters.ui.main.posts.postComments;

import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.Post;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.models.app_models.Workout;

public interface PostCommentCallback {
    void onExerciseClicked(Exercise exercise);
    void onWorkoutClicked(Workout workout);
    void onUserClicked(String creatorId);
    void onPostLike(Post post);
    void onPostComment(Post post);
}
