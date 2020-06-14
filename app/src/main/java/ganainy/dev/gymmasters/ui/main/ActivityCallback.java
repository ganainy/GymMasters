package ganainy.dev.gymmasters.ui.main;

public interface ActivityCallback {
    void openLoggedUserWorkoutsFragment(String loggedUserId);
    void openLoggedUserExercisesFragment(String loggedUserId);
    void openCreateWorkoutFragment();
    void openCreateExerciseFragment();
}
