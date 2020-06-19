package ganainy.dev.gymmasters.ui.main;

import ganainy.dev.gymmasters.models.app_models.Exercise;

public interface ActivityCallback {
    void openLoggedUserWorkoutsFragment(String loggedUserId);
    void openLoggedUserExercisesFragment(String loggedUserId);
    void openCreateWorkoutFragment();
    void openCreateExerciseFragment();
    void openExerciseFragment(Exercise exercise);
}
