package com.feicuiedu.readgroup.user.login;


import android.support.annotation.NonNull;

import com.feicuiedu.apphx.basemvp.MvpPresenter;
import com.feicuiedu.apphx.model.HxUserManager;
import com.feicuiedu.apphx.model.event.HxErrorEvent;
import com.feicuiedu.apphx.model.event.HxEventType;
import com.feicuiedu.apphx.model.event.HxSimpleEvent;
import com.feicuiedu.readgroup.hx.UserConverter;
import com.feicuiedu.readgroup.network.BombClient;
import com.feicuiedu.readgroup.network.event.LoginEvent;
import com.hyphenate.easeui.domain.EaseUser;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

class LoginPresenter extends MvpPresenter<LoginView> {


    @NonNull @Override protected LoginView getNullObject() {
        return LoginView.NULL;
    }

    private String password;

    public void login(@NonNull String username, @NonNull String password) {
        this.password = password;
        getView().startLoading();
        BombClient.getInstance().asyncLogin(username, password);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginEvent event) {
        if (event.success) {
            // 如果登录应用服务器成功，需要再登录环信服务器
            EaseUser easeUser = UserConverter.convert(event.result);

            HxUserManager.getInstance()
                    .asyncLogin(easeUser, password);
        } else {
            this.password = null;
            getView().stopLoading();
            getView().showLoginFail(event.errorMessage);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HxSimpleEvent event) {

        // 判断是否是登录成功事件
        if (event.type != HxEventType.LOGIN) return;

        this.password = null;
        getView().stopLoading();
        getView().navigateToHome();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HxErrorEvent event) {

        // 判断是否是登录失败事件
        if (event.type != HxEventType.LOGIN) return;

        this.password = null;
        getView().stopLoading();
        getView().showLoginFail(event.toString());
    }


}
