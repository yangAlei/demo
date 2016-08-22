package com.feicuiedu.apphx;


import android.app.Application;

import com.feicuiedu.apphx.model.repository.DefaultLocalUsersRepo;
import com.feicuiedu.apphx.model.repository.MockRemoteUsersRepo;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;

import timber.log.Timber;

public class HxBaseApplication extends Application{

    @Override public void onCreate() {
        super.onCreate();

        // 初始化Timber日志
        Timber.plant(new Timber.DebugTree());

        initEaseUI();

        HxModuleInitializer.getInstance()
                .setRemoteUsersRepo(new MockRemoteUsersRepo())
                .setLocalUsersRepo(DefaultLocalUsersRepo.getInstance(this))
                .init();
    }


    /**
     * 初始化环信SDK和EaseUI库
     */
    private void initEaseUI(){
        EMOptions options = new EMOptions();
        // 关闭自动登录
        options.setAutoLogin(false);
        EaseUI.getInstance().init(this, options);

        // 关闭环信日志
        EMClient.getInstance().setDebugMode(false);
    }
}
