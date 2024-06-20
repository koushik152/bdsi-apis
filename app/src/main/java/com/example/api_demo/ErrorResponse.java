package com.example.api_demo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ErrorResponse {
    @SerializedName("formExceptions")
    private List<FormException> formExceptions;

    @SerializedName("formError")
    private boolean formError;

    public List<FormException> getFormExceptions() {
        return formExceptions;
    }

    public boolean isFormError() {
        return formError;
    }

    public static class FormException {
        @SerializedName("localizedMessage")
        private String localizedMessage;

        @SerializedName("errorCode")
        private String errorCode;

        public String getLocalizedMessage() {
            return localizedMessage;
        }

        public String getErrorCode() {
            return errorCode;
        }
    }
}
