package com.feicuiedu.apphx.model.event;


import java.util.List;

public class HxSearchContactEvent {

    public final List<String> contacts;
    public final boolean isSuccess;
    public final String errorMessage;

    public HxSearchContactEvent(List<String> contacts) {
        this.contacts = contacts;
        this.isSuccess = true;
        this.errorMessage = null;
    }

    public HxSearchContactEvent(String errorMessage) {
        this.contacts = null;
        this.isSuccess = false;
        this.errorMessage = errorMessage;
    }
}
