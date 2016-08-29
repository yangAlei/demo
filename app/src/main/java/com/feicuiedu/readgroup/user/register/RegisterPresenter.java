package com.feicuiedu.readgroup.user.register;


import android.support.annotation.NonNull;

import com.feicuiedu.apphx.basemvp.MvpPresenter;
import com.feicuiedu.readgroup.network.BombClient;
import com.feicuiedu.readgroup.network.event.RegisterEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

class RegisterPresenter extends MvpPresenter<RegisterView> {


    @NonNull @Override protected RegisterView getNullObject() {
        return RegisterView.NULL;
    }

    public void register(@NonNull String username, @NonNull String password) {
        getView().startLoading();
        BombClient.getInstance().asyncRegister(username, password);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RegisterEvent event) {
        getView().stopLoading();

        if (event.success) {
            getView().showRegisterSuccess();
            getView().close();
        } else {
            getView().showRegisterFail(event.errorMessage);
        }
    }
}
