package ganainy.dev.gymmasters.models.app_models;

public class Post {

    Exercise exercise;
    Workout workout;
    Integer EntityType; //0 exercise,1 workout
    Long dateStamp;


    public Post(Exercise exercise, Integer entityType, Long dateStamp) {
        this.exercise = exercise;
        EntityType = entityType;
        this.dateStamp = dateStamp;
    }

    public Post(Workout workout, Integer entityType, Long dateStamp) {
        this.workout = workout;
        EntityType = entityType;
        this.dateStamp = dateStamp;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public Long getDateStamp() {
        return dateStamp;
    }

    public Integer getEntityType() {
        return EntityType;
    }

    public Workout getWorkout() {
        return workout;
    }
}
