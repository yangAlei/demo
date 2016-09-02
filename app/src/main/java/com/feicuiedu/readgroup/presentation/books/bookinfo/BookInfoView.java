package com.feicuiedu.readgroup.presentation.books.bookinfo;


import com.feicuiedu.apphx.basemvp.MvpView;
import com.feicuiedu.readgroup.network.entity.BookEntity;
import com.feicuiedu.readgroup.network.entity.UserEntity;

import java.util.List;

public interface BookInfoView extends MvpView{

    void showBookInfo(BookEntity bookEntity, List<UserEntity> likes);

    void setRefreshing(boolean refreshing);

    void showGetBookInfoFail(String msg);

    void toggleLike(boolean showLike);

    void showLikeActionSuccess(boolean isLike);

    void showLikeActionFail(boolean isLike, String msg);

    void showSendInviteResult(boolean success);

    BookInfoView NULL = new BookInfoView() {
        @Override public void showBookInfo(BookEntity bookEntity, List<UserEntity> likes) {
        }

        @Override public void setRefreshing(boolean refreshing) {
        }

        @Override public void showGetBookInfoFail(String msg) {
        }

        @Override public void toggleLike(boolean showLike) {
        }

        @Override public void showLikeActionSuccess(boolean isLike) {
        }

        @Override public void showLikeActionFail(boolean isLike, String msg) {
        }

        @Override public void showSendInviteResult(boolean success) {
        }
    };
}
