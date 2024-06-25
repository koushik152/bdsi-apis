package com.example.api_demo;

import com.google.gson.annotations.SerializedName;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "response", strict = false)
public  class LogoutResponse {
    @Element(name ="is-logged-out")
    private Boolean isLoggedOut;

    public Boolean getIsLoggedOut() {
        return isLoggedOut;
    }
}