package ganainy.dev.gymmasters.models.app_models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class Post implements Parcelable {

    protected Post(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        exercise = in.readParcelable(Exercise.class.getClassLoader());
        workout = in.readParcelable(Workout.class.getClassLoader());
        if (in.readByte() == 0) {
            EntityType = null;
        } else {
            EntityType = in.readInt();
        }
        if (in.readByte() == 0) {
            dateStamp = null;
        } else {
            dateStamp = in.readLong();
        }
        byte tmpIsLiked = in.readByte();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Long id;
    private Exercise exercise;
    private Workout workout;
    private Integer EntityType; //0 exercise,1 workout
    private Long dateStamp;

    public Post(Exercise exercise, Integer entityType, Long dateStamp,Boolean isLiked) {
        this.exercise = exercise;
        EntityType = entityType;
        this.dateStamp = dateStamp;
        id=UUID.randomUUID().getLeastSignificantBits();
    }

    public Post(Workout workout, Integer entityType, Long dateStamp,Boolean isLiked) {
        this.workout = workout;
        EntityType = entityType;
        this.dateStamp = dateStamp;
        id=UUID.randomUUID().getLeastSignificantBits();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeParcelable(exercise, flags);
        dest.writeParcelable(workout, flags);
        if (EntityType == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(EntityType);
        }
        if (dateStamp == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(dateStamp);
        }
    }
}
