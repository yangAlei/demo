package com.feicuiedu.apphx.model.event;


import java.util.List;

/**
 * 搜索联系人事件。如果搜索成功，返回环信id列表；如果搜索失败，返回错误信息。
 */
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
