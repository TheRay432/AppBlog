package com.example.appblog.Model;

import com.google.firebase.database.ServerValue;

public class Post {
    private String userKey;
    private String userName;
    private String title;
    private String Description;
    private String picture;
    private String userId;
    private String userPhoto;
    private Object timeStamp;

    public Post() {
    }

    public Post(String userName, String title, String description, String picture, String userId, String userPhoto) {
        this.userName=userName;
        this.title = title;
        Description = description;
        this.picture = picture;
        this.userId = userId;
        this.userPhoto = userPhoto;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserKey() {
        return userKey;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return Description;
    }

    public String getPicture() {
        return picture;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }
}
