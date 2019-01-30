package com.example.vikasgajjar.chatapp;

/**
 * Created by vikasgajjar on 2018-06-02.
 */

public class Message {
    private String userSent;

    private String message;
    private String sentUID;
    private String targetDisplayName;
    private String targetUID;
    private boolean sent;
    public Message(){

    }

    public Message(String message, String userSent, String targetDisplayName, String targetUID, String sentUID){
        this.message = message;
        this.userSent = userSent;
        this.targetDisplayName = targetDisplayName;
        this.targetUID = targetUID;
        this.sentUID = sentUID;

    }

    public void setSent(boolean sent){
        this.sent = sent;
    }

    public String getUserSent() {
        return userSent;
    }

    public void setUserSent(String userSent) {
        this.userSent = userSent;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSentUID(String sentUID) {
        this.sentUID = sentUID;
    }

    public void setTargetDisplayName(String targetDisplayName) {
        this.targetDisplayName = targetDisplayName;
    }

    public void setTargetUID(String targetUID) {
        this.targetUID = targetUID;
    }

    public String getMessage() {
        return message;
    }

    public String getSentUID() {
        return sentUID;
    }

    public String getTargetDisplayName() {
        return targetDisplayName;
    }

    public String getTargetUID() {
        return targetUID;
    }

    public boolean isSent() {
        return sent;
    }
}
