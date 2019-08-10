package com.example.myapplication.model;

public class User {
    private String name,email,rating;
    String id;
    private String photo;

    public User() {
    }

    public User(String id, String name, String email, String rating, String photo) {
        this.name = name;
        this.email = email;
        this.rating = rating;
        this.photo = photo;
        this.id = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
