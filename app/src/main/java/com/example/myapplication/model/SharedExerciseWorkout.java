package com.example.myapplication.model;

public class SharedExerciseWorkout {

    Exercise exercise;
    Workout workout;
    Integer EntityType; //0 exercise,1 workout
    Long dateStamp;


    public SharedExerciseWorkout(Exercise exercise, Integer entityType, Long dateStamp) {
        this.exercise = exercise;
        EntityType = entityType;
        this.dateStamp = dateStamp;
    }

    public SharedExerciseWorkout(Workout workout, Integer entityType, Long dateStamp) {
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
