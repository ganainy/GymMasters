package com.example.myapplication.model;

public class Workout {

    private String name, duration, exercisesNumber, level, photoLink;

    public Workout() {
    }

    public Workout(String name, String duration, String exercisesNumber, String level, String photoLink) {
        this.name = name;
        this.duration = duration;
        this.exercisesNumber = exercisesNumber;
        this.level = level;
        this.photoLink = photoLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getExercisesNumber() {
        return exercisesNumber;
    }

    public void setExercisesNumber(String exercisesNumber) {
        this.exercisesNumber = exercisesNumber;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }
}
