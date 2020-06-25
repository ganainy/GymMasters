package ganainy.dev.gymmasters.models.app_models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;

public class Exercise implements Parcelable{


    public Exercise() {
    }

    protected Exercise(Parcel in) {
        id = in.readString();
        name = in.readString();
        bodyPart = in.readString();
        execution = in.readString();
        mechanism = in.readString();
        previewPhotoOneUrl = in.readString();
        previewPhotoTwoUrl = in.readString();
        sets = in.readString();
        reps = in.readString();
        duration = in.readString();
        creatorId = in.readString();
        date = in.readString();
        creatorName = in.readString();
        additional_notes = in.readString();
        likerIdList = in.createStringArrayList();
        commentList = in.createTypedArrayList(Comment.CREATOR);
        creatorImageUrl = in.readString();
        byte tmpIsAddedToWorkout = in.readByte();
        isAddedToWorkout = tmpIsAddedToWorkout == 0 ? null : tmpIsAddedToWorkout == 1;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    private String name;
    private String bodyPart;
    private String execution;
    private String mechanism;
    private String previewPhotoOneUrl;
    private String previewPhotoTwoUrl;
    private String sets;
    private String reps;
    private String duration;
    private String creatorId;
    private String date;
    private String creatorName;
    private String additional_notes;
    private List<String> likerIdList;
    private List<Comment> commentList;
    private String creatorImageUrl;

    @Exclude()
    private Boolean isAddedToWorkout;


    public List<String> getLikerIdList() {
        return likerIdList;
    }

    public void setLikerIdList(List<String> likerIdList) {
        this.likerIdList = likerIdList;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }



    public String getCreatorImageUrl() {
        return creatorImageUrl;
    }
    public void setCreatorImageUrl(String creatorImageUrl) {
        this.creatorImageUrl = creatorImageUrl;
    }

    public Exercise(String name, String bodyPart, String execution, String mechanism, String previewPhotoOneUrl, String previewPhotoTwoUrl) {
        this.name = name;
        this.execution = execution;
        this.mechanism = mechanism;
        this.previewPhotoOneUrl = previewPhotoOneUrl;
        this.previewPhotoTwoUrl = previewPhotoTwoUrl;
        this.bodyPart = bodyPart;
    }



    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
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

    public String getPreviewPhotoOneUrl() {
        return previewPhotoOneUrl;
    }

    public void setPreviewPhotoOneUrl(String previewPhotoOneUrl) {
        this.previewPhotoOneUrl = previewPhotoOneUrl;
    }

    public String getPreviewPhotoTwoUrl() {
        return previewPhotoTwoUrl;
    }

    public void setPreviewPhotoTwoUrl(String previewPhotoTwoUrl) {
        this.previewPhotoTwoUrl = previewPhotoTwoUrl;
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

    public Boolean getIsAddedToWorkout() {
        return isAddedToWorkout;
    }

    public void setIsAddedToWorkout(Boolean isAddedToWorkout) {
        this.isAddedToWorkout = isAddedToWorkout;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(bodyPart);
        dest.writeString(execution);
        dest.writeString(mechanism);
        dest.writeString(previewPhotoOneUrl);
        dest.writeString(previewPhotoTwoUrl);
        dest.writeString(sets);
        dest.writeString(reps);
        dest.writeString(duration);
        dest.writeString(creatorId);
        dest.writeString(date);
        dest.writeString(creatorName);
        dest.writeString(additional_notes);
        dest.writeStringList(likerIdList);
        dest.writeTypedList(commentList);
        dest.writeString(creatorImageUrl);
        dest.writeByte((byte) (isAddedToWorkout == null ? 0 : isAddedToWorkout ? 1 : 2));
    }
}
