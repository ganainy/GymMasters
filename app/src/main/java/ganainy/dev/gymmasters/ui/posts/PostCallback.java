package ganainy.dev.gymmasters.ui.posts;

import ganainy.dev.gymmasters.models.app_models.Exercise;

public interface PostCallback {
    void onExerciseClicked(Exercise exercise, Integer adapterPosition);
}
