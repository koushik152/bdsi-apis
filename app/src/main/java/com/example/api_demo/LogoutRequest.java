package com.example.api_demo;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "logoutRequest")
public class LogoutRequest {

    @Element(name = "is_cmp_admin_login_param")
    private String isCmpAdminLoginParam;

    @Element(name = "login")
    private String login;

    @Element(name = "password")
    private String password;

    @Element(name = "atg-rest-output")
    private String atgRestOutput;

    public LogoutRequest(String isCmpAdminLoginParam, String login, String password, String atgRestOutput) {
        this.isCmpAdminLoginParam = isCmpAdminLoginParam;
        this.login = login;
        this.password = password;
        this.atgRestOutput = atgRestOutput;
    }

    public String getIsCmpAdminLoginParam() {
        return isCmpAdminLoginParam;
    }

    public void setIsCmpAdminLoginParam(String isCmpAdminLoginParam) {
        this.isCmpAdminLoginParam = isCmpAdminLoginParam;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAtgRestOutput() {
        return atgRestOutput;
    }

    public void setAtgRestOutput(String atgRestOutput) {
        this.atgRestOutput = atgRestOutput;
    }
}
