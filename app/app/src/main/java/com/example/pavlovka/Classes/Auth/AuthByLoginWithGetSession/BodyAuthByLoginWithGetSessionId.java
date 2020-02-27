package com.example.pavlovka.Classes.Auth.AuthByLoginWithGetSession;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BodyAuthByLoginWithGetSessionId {
    @SerializedName("sessionId")
    @Expose
    public String sessionId;

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    public String getSessionId() {
        return sessionId;
    }
}
