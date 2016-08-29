package com.feicuiedu.readgroup.network.event;


public class RegisterEvent {


    public final boolean success;
    public final String errorMessage;

    public RegisterEvent(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }
}
