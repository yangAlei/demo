package com.feicuiedu.readgroup.network.event;


public class UploadFileEvent {

    public final boolean success;
    public final String errorMessage;
    public final String url;

    public UploadFileEvent(boolean success, String errorMessage, String url) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.url = url;
    }
}
