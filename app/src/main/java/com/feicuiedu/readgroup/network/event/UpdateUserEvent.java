package com.feicuiedu.readgroup.network.event;


public class UpdateUserEvent {

    public final boolean success;
    public final String errorMessage;
    public final String avatar;

    public UpdateUserEvent(boolean success, String errorMessage, String avatar) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.avatar = avatar;
    }
}
