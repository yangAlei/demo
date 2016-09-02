package com.feicuiedu.readgroup.network.event;


import com.feicuiedu.readgroup.network.entity.BookEntity;

import java.util.List;

public class GetBooksEvent {

    public final boolean success;
    public final String errorMessage;
    public final List<BookEntity> books;

    public GetBooksEvent(boolean success, String errorMessage, List<BookEntity> books) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.books = books;
    }
}
