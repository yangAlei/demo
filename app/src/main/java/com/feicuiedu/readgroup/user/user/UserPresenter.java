package com.feicuiedu.readgroup.user.user;


import android.support.annotation.NonNull;

import com.feicuiedu.apphx.basemvp.MvpPresenter;
import com.feicuiedu.apphx.model.HxMessageManager;
import com.feicuiedu.apphx.model.HxUserManager;
import com.feicuiedu.readgroup.network.BombClient;
import com.feicuiedu.readgroup.network.event.UpdateUserEvent;
import com.feicuiedu.readgroup.network.event.UploadFileEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

public class UserPresenter extends MvpPresenter<UserView>{

    private final BombClient bombClient;
    private final HxUserManager hxUserManager;

    public UserPresenter(){
        bombClient = BombClient.getInstance();
        hxUserManager = HxUserManager.getInstance();
    }

    @NonNull @Override protected UserView getNullObject() {
        return UserView.NULL;
    }

    public void updateUser(){
        String hxId = hxUserManager.getCurrentUserId();
        getView().refreshUser(hxId);
    }

    public void updateAvatar(File file){
        getView().startLoading();
        bombClient.asyncUploadFile(file);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UploadFileEvent event){
        if (event.success) {
            String hxId = hxUserManager.getCurrentUserId();
            bombClient.asyncUpdateUser(hxId, event.url);
        } else {
            getView().stopLoading();
            getView().showUpdateAvatarFail(event.errorMessage);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateUserEvent event){
        getView().stopLoading();

        if (event.success) {
            String hxId = hxUserManager.getCurrentUserId();
            HxMessageManager.getInstance().sendAvatarUpdateMessage(event.avatar);
            hxUserManager.updateAvatar(event.avatar);
            getView().refreshUser(hxId);
        } else {
            getView().showUpdateAvatarFail(event.errorMessage);
        }
    }
}
