package com.example.pavlovka;

import com.example.pavlovka.Classes.GetSessionidd.SessionJson;
import com.example.pavlovka.Classes.Message;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface mxApi{
    @GET("api/transport")
    Call<SessionJson> getSessionId(@Query("message") String message);

    @GET("api/transport")
    Call<Message> message(@Query("message") String message);
}
