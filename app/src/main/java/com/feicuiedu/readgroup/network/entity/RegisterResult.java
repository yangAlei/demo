package com.feicuiedu.readgroup.network.entity;

/**
 * 用户注册的结果
 */
@SuppressWarnings("unused")
public class RegisterResult {

    private boolean success;
    private String error;
    private String data; // 用户的objectId

    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

    public String getData() {
        return data;
    }
}
