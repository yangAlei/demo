package com.feicuiedu.readgroup.network.event;


import com.feicuiedu.readgroup.network.entity.BookEntity;
import com.feicuiedu.readgroup.network.entity.UserEntity;

import java.util.List;

public class GetBookInfoEvent {

    public final boolean success;
    public final String errorMessage;
    public final List<UserEntity> likes;
    public final BookEntity book;

    public GetBookInfoEvent(String errorMessage) {
        this.success = false;
        this.errorMessage = errorMessage;
        this.likes = null;
        this.book = null;
    }

    public GetBookInfoEvent(List<UserEntity> likes, BookEntity book) {
        this.success = true;
        this.errorMessage = null;
        this.likes = likes;
        this.book = book;
    }
}
