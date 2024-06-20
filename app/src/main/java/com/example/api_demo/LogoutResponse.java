package com.example.api_demo;

import com.google.gson.annotations.SerializedName;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

public class LogoutResponse {
    @SerializedName("is-logged-out")
    private String isLoggedOut;

    public String getIsLoggedOut() {
        return isLoggedOut;
    }
}