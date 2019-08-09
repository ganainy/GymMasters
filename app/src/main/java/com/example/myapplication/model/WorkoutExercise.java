package com.example.myapplication.model;

public class WorkoutExercise {
    private String name, reps, sets, duration, targetMuscle;

    public WorkoutExercise(String name, String reps, String sets, String duration, String targetMuscle) {
        this.name = name;
        this.reps = reps;
        this.sets = sets;
        this.duration = duration;
        this.targetMuscle = targetMuscle;
    }

    public WorkoutExercise() {
    }

    public String getTargetMuscle() {
        return targetMuscle;
    }

    public void setTargetMuscle(String targetMuscle) {
        this.targetMuscle = targetMuscle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public String getSets() {
        return sets;
    }

    public void setSets(String sets) {
        this.sets = sets;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
