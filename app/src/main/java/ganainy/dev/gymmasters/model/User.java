package ganainy.dev.gymmasters.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String name, email, followers, following, about_me;
    String id;
    private String photo;

    public User() {
    }

    public User(String id, String name, String email, String photo) {
        this.name = name;
        this.email = email;
        this.photo = photo;
        this.id = id;
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

    protected User(Parcel in) {
        name = in.readString();
        email = in.readString();
        followers = in.readString();
        following = in.readString();
        about_me = in.readString();
        id = in.readString();
        photo = in.readString();
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
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
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(followers);
        parcel.writeString(following);
        parcel.writeString(about_me);
        parcel.writeString(id);
        parcel.writeString(photo);
    }
}
