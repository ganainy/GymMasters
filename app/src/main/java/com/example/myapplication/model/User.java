package com.example.myapplication.model;

import android.net.Uri;

public class User {
    private String name,email,rating;
    private String photo;

    public User() {
    }

    public User(String name, String email, String rating, String photo) {
        this.name = name;
        this.email = email;
        this.rating = rating;
        this.photo = photo;
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

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
