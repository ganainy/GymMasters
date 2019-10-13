package com.example.myapplication.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Exercise implements Parcelable {

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };
    Bitmap previewBitmap;


    public Exercise() {
    }

    private String name, bodyPart, execution, mechanism, previewPhoto1, previewPhoto2, sets, reps, duration, creatorId, date, creatorName, additional_notes;

    public Exercise(String name, String bodyPart, String execution, String mechanism, String previewPhoto1, String previewPhoto2) {
        this.name = name;
        this.execution = execution;
        this.mechanism = mechanism;
        this.previewPhoto1 = previewPhoto1;
        this.previewPhoto2 = previewPhoto2;
        this.bodyPart = bodyPart;
    }

    protected Exercise(Parcel in) {
        name = in.readString();
        bodyPart = in.readString();
        execution = in.readString();
        mechanism = in.readString();
        previewPhoto1 = in.readString();
        previewPhoto2 = in.readString();
        sets = in.readString();
        reps = in.readString();
        duration = in.readString();
        creatorId = in.readString();
        date = in.readString();
        creatorName = in.readString();
        additional_notes = in.readString();
        previewBitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }


    public Bitmap getPreviewBitmap() {
        return previewBitmap;
    }

    public void setPreviewBitmap(Bitmap previewBitmap) {
        this.previewBitmap = previewBitmap;
    }

    public String getBodyPart() {
        return bodyPart;
    }

    public void setBodyPart(String bodyPart) {
        this.bodyPart = bodyPart;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExecution() {
        return execution;
    }

    public void setExecution(String execution) {
        this.execution = execution;
    }



    public String getMechanism() {
        return mechanism;
    }

    public void setMechanism(String mechanism) {
        this.mechanism = mechanism;
    }



    public String getPreviewPhoto1() {
        return previewPhoto1;
    }

    public void setPreviewPhoto1(String previewPhoto1) {
        this.previewPhoto1 = previewPhoto1;
    }

    public String getPreviewPhoto2() {
        return previewPhoto2;
    }

    public void setPreviewPhoto2(String previewPhoto2) {
        this.previewPhoto2 = previewPhoto2;
    }



    public String getSets() {
        return sets;
    }

    public void setSets(String sets) {
        this.sets = sets;
    }

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getAdditional_notes() {
        return additional_notes;
    }

    public void setAdditional_notes(String additional_notes) {
        this.additional_notes = additional_notes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(bodyPart);
        parcel.writeString(execution);
        parcel.writeString(mechanism);
        parcel.writeString(previewPhoto1);
        parcel.writeString(previewPhoto2);
        parcel.writeString(sets);
        parcel.writeString(reps);
        parcel.writeString(duration);
        parcel.writeString(creatorId);
        parcel.writeString(date);
        parcel.writeString(creatorName);
        parcel.writeString(additional_notes);
        parcel.writeParcelable(previewBitmap, i);
    }
}
