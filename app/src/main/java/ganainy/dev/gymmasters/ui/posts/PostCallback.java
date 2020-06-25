package ganainy.dev.gymmasters.ui.posts;

import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.Post;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.models.app_models.Workout;

public interface PostCallback {
    void onExerciseClicked(Exercise exercise, Integer adapterPosition);
    void onWorkoutClicked(Workout workout, Integer adapterPosition);
    void onUserClicked(User user);
    void onPostLike(Post post, Integer adapterPosition);
    void onPostComment(Post post, Integer adapterPosition);
}
