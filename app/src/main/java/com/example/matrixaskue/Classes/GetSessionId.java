package com.example.matrixaskue.Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetSessionId {

    @SerializedName("login")
    @Expose
    public String login;

    @SerializedName("password")
    @Expose
    public String password;

    public void setLoginPassword(String login, String password) {
        this.login = login;
        this.password = password;
    }
    //-------get

    @SerializedName("user")
    @Expose
    public Object user;

    @SerializedName("sessionId")
    @Expose
    public String sessionId;

    @SerializedName("message")
    @Expose
    public String message;


    public Object getUser() {
        return user;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getMessage() {
        return message;
    }

}