package com.example.api_demo;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;



@Root(name = "SoftAllocationResponseDetails", strict = false)

public class LoginResponse {
        @Element(name = "response")
        private Response response;

        // Getters and Setters
        public Response getResponse() {
                return response;
        }
        @Root(name = "response", strict = false)
        public static class Response {
                @Element(name = "JSESSIONID")
                private String JSESSIONID;
                @Element(name = "key")
                private String key;
                @Element(name = "forcePasswordChange")
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

}