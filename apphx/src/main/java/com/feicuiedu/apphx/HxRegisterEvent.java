package com.feicuiedu.apphx;


import com.hyphenate.exceptions.HyphenateException;

/**
 * 事件：环信注册结果
 */
public class HxRegisterEvent {

    private final boolean success;

    private HyphenateException exception;

    public HxRegisterEvent() {
        success = true;
    }

    public HxRegisterEvent(HyphenateException exception) {
        this.exception = exception;
        success = false;
    }

    public boolean isSuccess() {
        return success;
    }

    public HyphenateException getException() {
        return exception;
    }
}
