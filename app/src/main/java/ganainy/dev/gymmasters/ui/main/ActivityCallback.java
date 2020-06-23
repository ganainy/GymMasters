package ganainy.dev.gymmasters.ui.main;

import ganainy.dev.gymmasters.models.app_models.Exercise;

public interface ActivityCallback {
    void openUserWorkoutsFragment(String userId,String userName);
    void openUserExercisesFragment(String userId,String userName);
    void openCreateWorkoutFragment();
    void openCreateExerciseFragment();
    void openExerciseFragment(Exercise exercise);
}
