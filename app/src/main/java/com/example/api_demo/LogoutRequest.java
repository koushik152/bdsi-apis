package com.example.api_demo;

import com.google.gson.annotations.SerializedName;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

public class LogoutRequest {
    @SerializedName("is_cmp_admin_login_param")
    private String isCmpAdminLoginParam;
    private String login;
    private String password;
    @SerializedName("atg-rest-output")
    private String atgRestOutput;

    public LogoutRequest(String isCmpAdminLoginParam, String login, String password, String atgRestOutput) {
        this.isCmpAdminLoginParam = isCmpAdminLoginParam;
        this.login = login;
        this.password = password;
        this.atgRestOutput = atgRestOutput;
    }

    // Getters and setters (optional)
}