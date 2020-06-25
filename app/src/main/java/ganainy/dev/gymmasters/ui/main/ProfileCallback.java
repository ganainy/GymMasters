package ganainy.dev.gymmasters.ui.main;

import ganainy.dev.gymmasters.models.app_models.Exercise;

public interface ProfileCallback {
    void openUserWorkoutsFragment(String userId,String userName);
    void openUserExercisesFragment(String userId,String userName);
    void openCreateWorkoutFragment();
    void openCreateExerciseFragment();
    void openExerciseFragment(Exercise exercise);
    void showLoggedUserFollowers(String key,String value);
    void showUsersFollowedByLoggedUser(String key,String value);
}
