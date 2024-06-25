package com.example.api_demo;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface CommonService {

    @POST("/rest/model/com/mobility/service/CommonService/login")
    Call<ResponseBody> login(@Body LoginRequest loginRequest);



    @POST("rest/model/com/mobility/service/CommonService/logout")
    Call<ResponseBody> logout(
            @Header("Cookie") String cookie,
            @Body LogoutRequest logoutRequest
    );
}
