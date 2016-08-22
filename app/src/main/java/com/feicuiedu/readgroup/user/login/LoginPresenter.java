package com.feicuiedu.readgroup.user.login;


import android.support.annotation.NonNull;

import com.feicuiedu.apphx.model.HxUserManager;
import com.feicuiedu.apphx.basemvp.MvpPresenter;
import com.feicuiedu.apphx.model.event.HxErrorEvent;
import com.feicuiedu.apphx.model.event.HxEventType;
import com.feicuiedu.apphx.model.event.HxSimpleEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

class LoginPresenter extends MvpPresenter<LoginView> {


    @NonNull @Override protected LoginView getNullObject() {
        return LoginView.NULL;
    }

    public void login(@NonNull String username, @NonNull String password) {
        getView().startLoading();
        HxUserManager.getInstance().asyncLogin(username, password);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HxSimpleEvent event) {

        // 判断是否是登录成功事件
        if (event.type != HxEventType.LOGIN) return;

        getView().stopLoading();
        getView().navigateToHome();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HxErrorEvent event) {

        // 判断是否是登录失败事件
        if (event.type != HxEventType.LOGIN) return;

        getView().stopLoading();
        getView().showLoginFail(event.toString());
    }

}
