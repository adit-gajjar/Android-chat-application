package com.example.vikasgajjar.chatapp;

import com.google.firebase.database.Exclude;

/**
 * Created by vikasgajjar on 2018-07-20.
 */

public class User {
    private String name;
    private String photoUrl;
    @Exclude
    private String uid;

    User(){}

    User(String name,String photoUrl){
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    public String getUid(){

        return uid;
    }
}
