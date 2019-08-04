package com.example.myapplication.model;

import java.io.Serializable;

public class Exercise  implements Serializable {
    String name,bodyPart,excecution,preperation,mechanism,utility,previewPhoto1,previewPhoto2,videoLink;

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
}
