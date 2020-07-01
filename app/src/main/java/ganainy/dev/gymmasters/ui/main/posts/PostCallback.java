package ganainy.dev.gymmasters.ui.main.posts;

import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.Post;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.models.app_models.Workout;

public interface PostCallback {
    void onExerciseClicked(Exercise exercise, Integer adapterPosition);
    void onWorkoutClicked(Workout workout, Integer adapterPosition);
    void onUserClicked(String postCreatorId);
    void onPostLike(Post post, Integer adapterPosition);
    void onPostComment(Post post, Integer adapterPosition);
}
