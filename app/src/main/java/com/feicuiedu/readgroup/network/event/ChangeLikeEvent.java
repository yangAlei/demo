package com.feicuiedu.readgroup.network.event;


import com.feicuiedu.readgroup.network.entity.BookEntity;

public class ChangeLikeEvent {

    public final boolean success;
    public final String errorMessage;
    public final boolean isLike;
    public final BookEntity bookEntity;

    public ChangeLikeEvent(boolean isLike, String errorMessage) {
        this.success = false;
        this.errorMessage = errorMessage;
        this.isLike = isLike;
        this.bookEntity = null;
    }

    public ChangeLikeEvent(boolean isLike, BookEntity bookEntity){
        this.success = true;
        this.errorMessage = null;
        this.isLike = isLike;
        this.bookEntity = bookEntity;
    }
}
