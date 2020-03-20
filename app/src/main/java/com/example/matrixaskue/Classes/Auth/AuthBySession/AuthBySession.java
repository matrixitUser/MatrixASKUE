package com.example.matrixaskue.Classes.Auth.AuthBySession;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthBySession{
    @SerializedName("sessionId")
    @Expose
    public String sessionId;

    @SerializedName("patronymic")
    @Expose
    public String patronymic;

    @SerializedName("surname")
    @Expose
    public String surname;

    @SerializedName("name")
    @Expose
    public String name;

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    public String getSessionId() {
        return sessionId;
    }

    public String getPatronymic() {
        return patronymic;
    }
    public String getSurname() {
        return surname;
    }
    public String getName() {
        return name;
    }
}