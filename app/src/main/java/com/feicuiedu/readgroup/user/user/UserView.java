package com.feicuiedu.readgroup.user.user;


import com.feicuiedu.apphx.basemvp.MvpView;

public interface UserView extends MvpView{

    void refreshUser(String hxId);

    void startLoading();

    void stopLoading();

    void showUpdateAvatarFail(String msg);

    UserView NULL = new UserView() {
        @Override public void refreshUser(String hxId) {
        }

        @Override public void startLoading() {
        }

        @Override public void stopLoading() {
        }

        @Override public void showUpdateAvatarFail(String msg) {
        }
    };
}
