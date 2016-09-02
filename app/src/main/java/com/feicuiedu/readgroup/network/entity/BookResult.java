package com.feicuiedu.readgroup.network.entity;


import java.util.List;

@SuppressWarnings("unused")
public class BookResult {

    private boolean success;
    private String error;
    private List<BookEntity> data;

    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

    public List<BookEntity> getData() {
        return data;
    }
}
