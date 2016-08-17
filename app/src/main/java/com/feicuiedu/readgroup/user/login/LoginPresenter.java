package com.feicuiedu.readgroup.user.login;


import android.support.annotation.NonNull;

import com.feicuiedu.apphx.HxLoginEvent;
import com.feicuiedu.apphx.HxUserManager;
import com.feicuiedu.apphx.basemvp.MvpPresenter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LoginPresenter extends MvpPresenter<LoginView>{


    @NonNull @Override protected LoginView getNullObject() {
        return LoginView.NULL;
    }

    public void login(@NonNull String username, @NonNull String password) {
        getView().startLoading();
        HxUserManager.getInstance().asyncLogin(username, password);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HxLoginEvent event) {
        getView().stopLoading();
        if (event.isSuccess()) {
            getView().navigateToHome();
        } else {
            String msg = String.format("code: %s %s",
                    event.getErrorCode(),
                    event.getErrorMessage());
            getView().showLoginFail(msg);
        }
    }
}
