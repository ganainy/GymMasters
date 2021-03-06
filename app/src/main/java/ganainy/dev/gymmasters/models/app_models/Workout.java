package ganainy.dev.gymmasters.models.app_models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Workout implements Parcelable {


    public Workout() {
    }

    public Workout(String name, String duration, String exercisesNumber, String level, String photoLink) {
        this.name = name;
        this.duration = duration;
        this.exercisesNumber = exercisesNumber;
        this.level = level;
        this.photoLink = photoLink;
    }

    private String id;
    private String name;
    private String duration;
    private String exercisesNumber;
    private String level;
    private String photoLink;
    private String creatorId;
    private String date;
    private String creatorName;
    private String creatorImageUrl;

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

    private List<String> likerIdList;
    private List<Comment> commentList;


    public String getCreatorImageUrl() {
        return creatorImageUrl;
    }

    public void setCreatorImageUrl(String creatorImageUrl) {
        this.creatorImageUrl = creatorImageUrl;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    private List<Exercise> workoutExerciseList;

    protected Workout(Parcel in) {
        id = in.readString();
        name = in.readString();
        duration = in.readString();
        exercisesNumber = in.readString();
        level = in.readString();
        photoLink = in.readString();
        creatorImageUrl = in.readString();
        workoutExerciseList = in.createTypedArrayList(Exercise.CREATOR);
    }

    public List<Exercise> getWorkoutExerciseList() {
        return workoutExerciseList;
    }

    public void setWorkoutExerciseList(List<Exercise> workoutExerciseList) {
        this.workoutExerciseList = workoutExerciseList;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(duration);
        dest.writeString(exercisesNumber);
        dest.writeString(level);
        dest.writeString(photoLink);
        dest.writeString(creatorId);
        dest.writeString(date);
        dest.writeString(creatorName);
        dest.writeString(creatorImageUrl);
        dest.writeStringList(likerIdList);
        dest.writeTypedList(commentList);
        dest.writeTypedList(workoutExerciseList);
    }
}
