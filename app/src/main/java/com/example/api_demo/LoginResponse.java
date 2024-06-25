package com.example.api_demo;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "SoftAllocationResponseDetails", strict = false)
public class LoginResponse {

        @Element(name = "JSESSIONID" ,required = false)
        private String JSESSIONID;

        @Element(name = "key" ,required = false)
        private String key;

        @Element(name = "forcePasswordChange" ,required = false)
        private boolean forcePasswordChange;


        // Getters and Setters
        public String getJSESSIONID() {
            return JSESSIONID;
        }



        public String getKey() {
            return key;
        }



        public boolean isForcePasswordChange() {
            return forcePasswordChange;
        }



}

