package ganainy.dev.gymmasters.ui.main;

import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.Post;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.models.app_models.Workout;

public interface ActivityCallback {
    void openUserWorkoutsFragment(String userId,String userName);
    void openUserExercisesFragment(String userId,String userName);
    void openCreateWorkoutFragment();
    void openCreateExerciseFragment();
    void openExerciseFragment(Exercise exercise);
    void showLoggedUserFollowers(String key,String value);
    void showUsersFollowedByLoggedUser(String key,String value);
    void openYoutubeFragment(String exerciseName);
    void onOpenFindUserFragment(String filterType);
    void onOpenPostCommentFragment(Post post);
    void onOpenWorkoutFragment(Workout workout);
    void onOpenMuscleFragment(String muscleName);
    void onOpenUserFragment(User user);
}
