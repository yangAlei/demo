package com.feicuiedu.apphx;


import android.app.ActivityManager;
import android.app.Application;
import android.content.Intent;

import com.feicuiedu.apphx.easeui.HxNotificationInfoProvider;
import com.feicuiedu.apphx.model.repository.DefaultLocalInviteRepo;
import com.feicuiedu.apphx.model.repository.DefaultLocalUsersRepo;
import com.feicuiedu.apphx.model.repository.MockRemoteUsersRepo;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;

import java.util.List;

import timber.log.Timber;

public class HxBaseApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();

        if (isAppProcess()) {
            // 初始化Timber日志
            Timber.plant(new Timber.DebugTree());

            // 初始化EaseUI
            initEaseUI();

            // 初始化AppHx模块
            HxModuleInitializer.getInstance()
                    .setRemoteUsersRepo(new MockRemoteUsersRepo())
                    .setLocalUsersRepo(DefaultLocalUsersRepo.getInstance(this))
                    .setLocalInviteRepo(DefaultLocalInviteRepo.getInstance(this))
                    .init();

            // 启动后台服务，处理通知等
            Intent service = new Intent(this, HxMainService.class);
            startService(service);
        }

    }


    /**
     * 初始化环信SDK和EaseUI库
     */
    private void initEaseUI() {
        EMOptions options = new EMOptions();
        // 关闭自动登录
        options.setAutoLogin(false);

        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);

        EaseUI.getInstance().init(this, options);

        EaseUI.getInstance().getNotifier().setNotificationInfoProvider(new HxNotificationInfoProvider(this));

        // 关闭环信日志
        EMClient.getInstance().setDebugMode(false);
    }

    private boolean isAppProcess() {
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果APP启用了远程的service，此application:onCreate会被调用2次，例如使用百度定位服务时。
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次。
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回。
        //noinspection RedundantIfStatement
        if (processAppName == null || !processAppName.equalsIgnoreCase(getPackageName())) {
            // 则此application::onCreate 是被service 调用的。
            return false;
        }
        return true;
    }

    // 获取进程名称
    private String getAppName(int pID) {
        String processName;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : processes) {

            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
