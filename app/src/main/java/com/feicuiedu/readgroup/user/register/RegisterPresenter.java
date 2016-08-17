package com.feicuiedu.readgroup.user.register;


import android.support.annotation.NonNull;

import com.feicuiedu.apphx.HxRegisterEvent;
import com.feicuiedu.apphx.HxUserManager;
import com.feicuiedu.apphx.basemvp.MvpPresenter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class RegisterPresenter extends MvpPresenter<RegisterView>{


    @NonNull @Override protected RegisterView getNullObject() {
        return RegisterView.NULL;
    }

    public void register(@NonNull String username, @NonNull String password) {
        getView().startLoading();
        HxUserManager.getInstance().asyncRegister(username, password);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HxRegisterEvent event) {
        getView().stopLoading();
        if (event.isSuccess()) {
            getView().showRegisterSuccess();
            getView().close();
        } else {
            String info = String.format("code = %s %s.",
                    event.getException().getErrorCode(),
                    event.getException().getDescription());
            getView().showRegisterFail(info);
        }
    }
}
