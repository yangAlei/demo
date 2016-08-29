package com.feicuiedu.readgroup.network.event;


import com.feicuiedu.readgroup.network.entity.UserEntity;

public class LoginEvent {

    public final boolean success;
    public final String errorMessage;
    public final UserEntity result;

    public LoginEvent(boolean success, String errorMessage, UserEntity result) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.result = result;
    }
}
