package com.example.admin.moments.models;

/**
 * Created by ADMIN on 2/25/2018.
 */

public class User {
    private String name;
    private String email;
    private String id;

    public User(){}

    public  User(String name,String email,String image){
        this.name=name;
        this.email=email;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
