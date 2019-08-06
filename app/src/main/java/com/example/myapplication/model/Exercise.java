package com.example.myapplication.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Exercise implements Parcelable {
    String name,bodyPart,excecution,preperation,mechanism,utility,previewPhoto1,previewPhoto2,videoLink;
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

    public Exercise() {
    }

    public Exercise(String name, String bodyPart, String excecution, String preperation, String mechanism, String utility, String previewPhoto1, String previewPhoto2, String videoLink) {
        this.name = name;
        this.excecution = excecution;
        this.preperation = preperation;
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
        excecution = in.readString();
        preperation = in.readString();
        mechanism = in.readString();
        utility = in.readString();
        previewPhoto1 = in.readString();
        previewPhoto2 = in.readString();
        videoLink = in.readString();
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

    public String getExcecution() {
        return excecution;
    }

    public void setExcecution(String excecution) {
        this.excecution = excecution;
    }

    public String getPreperation() {
        return preperation;
    }

    public void setPreperation(String preperation) {
        this.preperation = preperation;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(bodyPart);
        parcel.writeString(excecution);
        parcel.writeString(preperation);
        parcel.writeString(mechanism);
        parcel.writeString(utility);
        parcel.writeString(previewPhoto1);
        parcel.writeString(previewPhoto2);
        parcel.writeString(videoLink);
        parcel.writeParcelable(previewBitmap, i);
        parcel.writeParcelable(preview2Bitmap, i);
    }
}
