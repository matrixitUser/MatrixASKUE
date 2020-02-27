package com.example.pavlovka.Classes.Auth.AuthByLoginWithGetSession;

import com.example.pavlovka.Classes.HeadApi;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthByLoginWithGetSession {
    @SerializedName("head")
    @Expose
    public HeadApi head;

    @SerializedName("body")
    @Expose
    public BodyAuthByLoginWithGetSessionId body;

    public void setHead(String what) {
        HeadApi head = new HeadApi();
        head.setWhat(what);
        this.head = head;
    }

    public HeadApi getHead() {
        return head;
    }

    public void setBody(String sessionId) {
        BodyAuthByLoginWithGetSessionId body = new BodyAuthByLoginWithGetSessionId();
        body.setSessionId(sessionId);
        this.body = body;
    }

    public BodyAuthByLoginWithGetSessionId getBody() {
        return body;
    }
}