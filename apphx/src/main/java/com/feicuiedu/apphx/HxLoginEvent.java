package com.feicuiedu.apphx;

/**
 * 事件：环信登录结果
 */
public class HxLoginEvent {

    private final boolean success;

    private int errorCode;

    private String errorMessage;

    public HxLoginEvent() {
        this.success = true;
    }

    public HxLoginEvent(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.success = false;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @return true 登录成功； false 登录失败
     */
    public boolean isSuccess() {
        return success;
    }
}

