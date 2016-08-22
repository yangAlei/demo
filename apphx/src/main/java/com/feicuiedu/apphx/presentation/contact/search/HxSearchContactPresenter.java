package com.feicuiedu.apphx.presentation.contact.search;


import android.support.annotation.NonNull;

import com.feicuiedu.apphx.basemvp.MvpPresenter;
import com.feicuiedu.apphx.model.HxContactManager;
import com.feicuiedu.apphx.model.event.HxSearchContactEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class HxSearchContactPresenter extends MvpPresenter<HxSearchContactView> {

    @NonNull @Override protected HxSearchContactView getNullObject() {
        return HxSearchContactView.NULL;
    }

    public void searchContact(final String query) {
        getView().startLoading();
        HxContactManager.getInstance().searchContacts(query);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HxSearchContactEvent event) {
        getView().stopLoading();

        if (event.isSuccess) {
            getView().showContacts(event.contacts);
        } else {
            getView().showSearchError(event.errorMessage);
        }
    }
}
