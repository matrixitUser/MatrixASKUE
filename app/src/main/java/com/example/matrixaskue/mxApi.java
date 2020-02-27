package com.example.matrixaskue;

import com.example.matrixaskue.Classes.GetSessionidd.SessionJson;
import com.example.matrixaskue.Classes.Message;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface mxApi{
    @GET("api/transport")
    Call<SessionJson> getSessionId(@Query("message") String message);

    @GET("api/transport")
    Call<Message> message(@Query("message") String message);
}
