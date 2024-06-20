package com.example.api_demo;

public class LoginRequest {
    private String atgRestOutput;
    private String login;
    private String password;

    public LoginRequest(String atgRestOutput, String login, String password) {
        this.atgRestOutput = atgRestOutput;
        this.login = login;
        this.password = password;
    }
}

