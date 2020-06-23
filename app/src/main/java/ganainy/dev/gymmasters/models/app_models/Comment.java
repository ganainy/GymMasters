package ganainy.dev.gymmasters.models.app_models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Comment implements Parcelable {

    public Comment() {
    }

    public String getCommenterId() {
        return commenterId;
    }

    public void setCommenterId(String commenterId) {
        this.commenterId = commenterId;
    }

    public Long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    String commenterId;
    Long dateCreated;
    String text;

    public Comment(String commenterId, Long dateCreated, String text) {
        this.commenterId = commenterId;
        this.dateCreated = dateCreated;
        this.text = text;
    }

    protected Comment(Parcel in) {
        commenterId = in.readString();
        if (in.readByte() == 0) {
            dateCreated = null;
        } else {
            dateCreated = in.readLong();
        }
        text = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(commenterId);
        if (dateCreated == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(dateCreated);
        }
        dest.writeString(text);
    }
}
