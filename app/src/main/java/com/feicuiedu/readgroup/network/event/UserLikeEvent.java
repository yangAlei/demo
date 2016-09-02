package com.feicuiedu.readgroup.network.event;


import com.feicuiedu.readgroup.network.entity.BookEntity;

import java.util.List;

public class UserLikeEvent {

    public final boolean success;
    public final String errorMessage;
    public final List<BookEntity> books;

    public UserLikeEvent(List<BookEntity> books, String errorMessage, boolean success) {
        this.books = books;
        this.errorMessage = errorMessage;
        this.success = success;
    }
}
