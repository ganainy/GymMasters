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
    String name, bodyPart, execution, preparation, mechanism, utility, previewPhoto1, previewPhoto2, videoLink, sets, reps, duration, creatorId;

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public Exercise() {
    }

    public Exercise(String name, String bodyPart, String execution, String preparation, String mechanism, String utility, String previewPhoto1, String previewPhoto2, String videoLink) {
        this.name = name;
        this.execution = execution;
        this.preparation = preparation;
        this.mechanism = mechanism;
        this.utility = utility;
        this.previewPhoto1 = previewPhoto1;
        this.previewPhoto2 = previewPhoto2;
        this.videoLink = videoLink;
        this.bodyPart = bodyPart;
    }
    Bitmap previewBitmap, preview2Bitmap;

    protected Exercise(Parcel in) {
        name = in.readString();
        bodyPart = in.readString();
        execution = in.readString();
        preparation = in.readString();
        mechanism = in.readString();
        utility = in.readString();
        previewPhoto1 = in.readString();
        previewPhoto2 = in.readString();
        videoLink = in.readString();
        sets = in.readString();
        reps = in.readString();
        duration = in.readString();
        previewBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        preview2Bitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public Bitmap getPreview2Bitmap() {
        return preview2Bitmap;
    }

    public void setPreview2Bitmap(Bitmap preview2Bitmap) {
        this.preview2Bitmap = preview2Bitmap;
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

    public String getPreparation() {
        return preparation;
    }

    public void setPreparation(String preparation) {
        this.preparation = preparation;
    }

    public String getMechanism() {
        return mechanism;
    }

    public void setMechanism(String mechanism) {
        this.mechanism = mechanism;
    }

    public String getUtility() {
        return utility;
    }

    public void setUtility(String utility) {
        this.utility = utility;
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

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(bodyPart);
        parcel.writeString(execution);
        parcel.writeString(preparation);
        parcel.writeString(mechanism);
        parcel.writeString(utility);
        parcel.writeString(previewPhoto1);
        parcel.writeString(previewPhoto2);
        parcel.writeString(videoLink);
        parcel.writeString(sets);
        parcel.writeString(reps);
        parcel.writeString(duration);
        parcel.writeParcelable(previewBitmap, i);
        parcel.writeParcelable(preview2Bitmap, i);
    }
}
