package de.ironicdev.amazebase.models;

import com.google.gson.Gson;

public class StatusMessage {
    private int errorCode;
    private boolean success;
    private String message;

    public StatusMessage(){
    }

    public StatusMessage(String msg, int errorCode, boolean success){
        this.message = msg;
        this.errorCode = errorCode;
        this.success = success;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
