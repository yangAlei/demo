package com.feicuiedu.readgroup.presentation.user.user;


import com.feicuiedu.apphx.basemvp.MvpView;
import com.feicuiedu.readgroup.network.entity.BookEntity;

import java.util.List;

public interface UserView extends MvpView{

    void refreshUser(String hxId);

    void startLoading();

    void stopLoading();

    void showUpdateAvatarFail(String msg);

    void setBooks(List<BookEntity> books);

    void setRefreshing(boolean refreshing);

    void showRefreshFail(String msg);

    UserView NULL = new UserView() {
        @Override public void refreshUser(String hxId) {
        }

        @Override public void startLoading() {
        }

        @Override public void stopLoading() {
        }

        @Override public void showUpdateAvatarFail(String msg) {
        }

        @Override public void setBooks(List<BookEntity> books) {
        }

        @Override public void setRefreshing(boolean refreshing) {
        }

        @Override public void showRefreshFail(String msg) {
        }
    };
}
