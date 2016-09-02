package com.feicuiedu.readgroup.network.entity;


import java.util.List;

@SuppressWarnings("unused")
public class BookInfoResult {

    private boolean success;
    private String error;
    private Data data;

    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        private BookEntity book;

        private List<UserEntity> likes;

        public BookEntity getBook() {
            return book;
        }

        public List<UserEntity> getLikes() {
            return likes;
        }
    }
}
