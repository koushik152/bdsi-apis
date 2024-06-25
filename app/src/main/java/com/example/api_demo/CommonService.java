package com.example.api_demo;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CommonService {//jai jai jai jai jai

    @POST("/rest/model/com/mobility/service/CommonService/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);



    @POST("rest/model/com/mobility/service/CommonService/logout")
    Call<LogoutResponse> logout(
            @Body LogoutRequest logoutRequest
    );

}
