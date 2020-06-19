package ganainy.dev.gymmasters.ui.specificWorkout;

import ganainy.dev.gymmasters.models.app_models.Exercise;

public interface ExerciseInsideWorkoutCallback {
    void onTimeExerciseClicked(Exercise exercise,Integer adapterPosition);
    void onRepsExerciseClicked(Exercise exercise,Integer adapterPosition);
}
