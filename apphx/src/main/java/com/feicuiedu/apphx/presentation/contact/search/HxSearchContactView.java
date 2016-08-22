package com.feicuiedu.apphx.presentation.contact.search;


import com.feicuiedu.apphx.basemvp.MvpView;

import java.util.List;

public interface HxSearchContactView extends MvpView{

    void startLoading();

    void stopLoading();

    /**
     * @param contacts 环信Id的集合
     */
    void showContacts(List<String> contacts);

    void showSearchError(String error);

    HxSearchContactView NULL = new HxSearchContactView() {
        @Override public void startLoading() {
        }

        @Override public void stopLoading() {
        }

        @Override public void showContacts(List<String> contacts) {
        }

        @Override public void showSearchError(String error) {
        }
    };
}