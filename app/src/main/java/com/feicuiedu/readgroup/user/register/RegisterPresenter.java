package com.feicuiedu.readgroup.user.register;


import android.support.annotation.NonNull;

import com.feicuiedu.apphx.model.HxUserManager;
import com.feicuiedu.apphx.basemvp.MvpPresenter;
import com.feicuiedu.apphx.model.event.HxErrorEvent;
import com.feicuiedu.apphx.model.event.HxEventType;
import com.feicuiedu.apphx.model.event.HxSimpleEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

class RegisterPresenter extends MvpPresenter<RegisterView> {


    @NonNull @Override protected RegisterView getNullObject() {
        return RegisterView.NULL;
    }

    public void register(@NonNull String username, @NonNull String password) {
        getView().startLoading();
        HxUserManager.getInstance().asyncRegister(username, password);
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HxSimpleEvent event) {

        // 判断是否是注册事件
        if (event.type != HxEventType.REGISTER) return;

        getView().stopLoading();
        getView().showRegisterSuccess();
        getView().close();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HxErrorEvent event) {

        // 判断是否是注册事件
        if (event.type != HxEventType.REGISTER) return;

        getView().stopLoading();
        getView().showRegisterFail(event.toString());
    }
}
