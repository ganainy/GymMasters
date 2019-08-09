package com.example.myapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Workout implements Parcelable {

    public static final Creator<Workout> CREATOR = new Creator<Workout>() {
        @Override
        public Workout createFromParcel(Parcel in) {
            return new Workout(in);
        }

        @Override
        public Workout[] newArray(int size) {
            return new Workout[size];
        }
    };

    public Workout() {
    }

    public Workout(String name, String duration, String exercisesNumber, String level, String photoLink) {
        this.name = name;
        this.duration = duration;
        this.exercisesNumber = exercisesNumber;
        this.level = level;
        this.photoLink = photoLink;
    }

    private String id, name, duration, exercisesNumber, level, photoLink;

    protected Workout(Parcel in) {
        id = in.readString();
        name = in.readString();
        duration = in.readString();
        exercisesNumber = in.readString();
        level = in.readString();
        photoLink = in.readString();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(duration);
        parcel.writeString(exercisesNumber);
        parcel.writeString(level);
        parcel.writeString(photoLink);
    }
}
