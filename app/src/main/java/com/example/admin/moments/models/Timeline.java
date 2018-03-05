package com.example.admin.moments.models;

/**
 * Created by ADMIN on 3/4/2018.
 */

public class Timeline {
    public String post;
    public String from;
    public String date;
    public String image;

    public Timeline() {
    }

    public Timeline(String post, String from, String date) {
        this.post = post;
        this.from = from;
        this.date = date;
    }

    public Timeline(String post, String from, String date, String image) {
        this.post = post;
        this.from = from;
        this.date = date;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

