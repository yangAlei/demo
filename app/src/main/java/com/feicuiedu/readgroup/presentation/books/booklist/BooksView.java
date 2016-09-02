package com.feicuiedu.readgroup.presentation.books.booklist;


import com.feicuiedu.apphx.basemvp.MvpView;
import com.feicuiedu.readgroup.network.entity.BookEntity;

import java.util.List;

public interface BooksView extends MvpView{

    void setBooks(List<BookEntity> books);

    void setRefreshing(boolean refreshing);

    void showRefreshFail(String msg);

    BooksView NULL = new BooksView() {
        @Override public void setBooks(List<BookEntity> books) {
        }

        @Override public void setRefreshing(boolean refreshing) {
        }

        @Override public void showRefreshFail(String msg) {
        }

    };
}
