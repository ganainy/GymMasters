package ganainy.dev.gymmasters.models.app_models;

import android.os.Parcel;
import android.os.Parcelable;

import retrofit2.http.Url;


public class User implements Parcelable {
    private String id, name, email, about_me, photo;
    private Long followers,following,rating;

    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        email = in.readString();
        about_me = in.readString();
        photo = in.readString();
        if (in.readByte() == 0) {
            followers = null;
        } else {
            followers = in.readLong();
        }
        if (in.readByte() == 0) {
            following = null;
        } else {
            following = in.readLong();
        }
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readLong();
        }
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public Long getRating() {
        return rating;
    }

    public void setRating(Long rating) {
        this.rating = rating;
    }


    public User() {
    }

    public User(String id, String name, String email, String photo) {
        this.name = name;
        this.email = email;
        this.photo = photo;
        this.id = id;
    }

    public User(String id, String name, String email) {
        this.name = name;
        this.email = email;
        this.id = id;
    }


    public Long getFollowers() {
        return followers;
    }

    public void setFollowers(Long followers) {
        this.followers = followers;
    }

    public Long getFollowing() {
        return following;
    }

    public void setFollowing(Long following) {
        this.following = following;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAbout_me() {
        return about_me;
    }

    public void setAbout_me(String about_me) {
        this.about_me = about_me;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(about_me);
        dest.writeString(photo);
        if (followers == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(followers);
        }
        if (following == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(following);
        }
        if (rating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(rating);
        }
    }
}
