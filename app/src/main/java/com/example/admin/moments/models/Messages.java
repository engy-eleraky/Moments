package com.example.admin.moments.models;

import java.io.Serializable;

/**
 * Created by ADMIN on 2/25/2018.
 */

public class Messages {
    private String messages,type,from,time;
    private boolean seen;
    public Messages(String messages,boolean seen,String type,String from,String time){
        this.messages=messages;
        this.seen=seen;
        this.type=type;
        this.from=from;
        this.time=time;
    }
    public Messages(){}

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
