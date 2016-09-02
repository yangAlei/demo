package com.feicuiedu.readgroup.presentation.books.booklist;


import android.support.annotation.NonNull;

import com.feicuiedu.apphx.basemvp.MvpPresenter;
import com.feicuiedu.readgroup.network.BombClient;
import com.feicuiedu.readgroup.network.entity.BookEntity;
import com.feicuiedu.readgroup.network.event.GetBooksEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class BooksPresenter extends MvpPresenter<BooksView>{


    private List<BookEntity> books;

    @NonNull @Override protected BooksView getNullObject() {
        return BooksView.NULL;
    }

    public void getBooks(boolean triggerByUser){

        if (!triggerByUser) {
            if (books != null) {
                getView().setBooks(books);
                return;
            }

            getView().setRefreshing(true);
        }

        BombClient.getInstance().asyncGetBooks();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetBooksEvent event) {
        getView().setRefreshing(false);
        if (event.success) {
            books = event.books;
            getView().setBooks(books);
        } else {
            getView().showRefreshFail(event.errorMessage);
        }
    }
}
