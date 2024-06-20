package com.example.api_demo;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
public interface CommonService {

    @POST("/rest/model/com/mobility/service/CommonService/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
    @POST("/rest/model/com/mobility/service/CommonService/logout")
    Call<LogoutResponse> logout(@Body LogoutRequest logoutRequest);
}
