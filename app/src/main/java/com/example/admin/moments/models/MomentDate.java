package com.example.admin.moments.models;

import java.io.Serializable;

/**
 * Created by ADMIN on 3/4/2018.
 */

public class MomentDate implements Serializable {
    public String title;
    public String date;
    public int remind;
    public int id;

    public MomentDate() {
    }

    public MomentDate(String title, String date, int remind) {
        this.title = title;
        this.date = date;
        this.remind = remind;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

