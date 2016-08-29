package com.feicuiedu.readgroup.network.entity;


import java.util.List;

@SuppressWarnings("unused")
public class SearchUserResult {

    private boolean success;
    private String error;
    private List<UserEntity> data;

    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

    public List<UserEntity> getData() {
        return data;
    }
}
