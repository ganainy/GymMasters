package ganainy.dev.gymmasters.ui.createWorkout;

import ganainy.dev.gymmasters.models.app_models.Exercise;

public interface AddExerciseCallback {
    void onExercisesAdded(Exercise exercise,Integer adapterPosition);
    void onExercisesDeleted(Exercise exercise,Integer adapterPosition);
}
