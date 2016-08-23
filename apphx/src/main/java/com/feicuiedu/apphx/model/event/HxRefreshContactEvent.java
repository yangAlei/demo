package com.feicuiedu.apphx.model.event;


import java.util.List;

/**
 * 刷新联系人事件。
 */
public final class HxRefreshContactEvent {

    // 联系人的环信id列表。
    public final List<String> contacts;

    // true代表联系人列表发生了变化
    public final boolean changed;

    public HxRefreshContactEvent(List<String> contacts) {
        this.contacts = contacts;
        this.changed = true;
    }

    @SuppressWarnings("unused")
    public HxRefreshContactEvent() {
        this.contacts = null;
        this.changed = false;
    }
}
