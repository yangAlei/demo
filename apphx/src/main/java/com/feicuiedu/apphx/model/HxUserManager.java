package com.feicuiedu.apphx.model;

import android.support.annotation.NonNull;

import com.feicuiedu.apphx.model.event.HxErrorEvent;
import com.feicuiedu.apphx.model.event.HxEventType;
import com.feicuiedu.apphx.model.event.HxSimpleEvent;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

/**
 * 环信用户基本功能管理：登录，注册，登出。
 *
 *
 */
public class HxUserManager implements EMConnectionListener {

    private static HxUserManager sInstance;

    public static HxUserManager getInstance() {
        if (sInstance == null) {
            sInstance = new HxUserManager();
        }
        return sInstance;
    }

    private final EMClient emClient;
    private final EventBus eventBus;
    private final ExecutorService executorService;

    // 当前登录用户的环信Id
    private String currentUserId;

    private HxUserManager() {
        emClient = EMClient.getInstance();
        emClient.addConnectionListener(this);

        eventBus = EventBus.getDefault();
        executorService = Executors.newSingleThreadExecutor();
    }

    // start-interface: EMConnectionListener
    @Override public void onConnected() {

    }

    @Override public void onDisconnected(int error) {
        Timber.d("onDisconnected error code: %d", error);
        switch (error){
            case EMError.USER_REMOVED: // 用户账号被删除
                break;
            case EMError.USER_LOGIN_ANOTHER_DEVICE: // 用户在其它设备登录
                break;
            default:
        }
    } // end-interface: EMConnectionListener

    public boolean isLogin(){
        // 返回是否登录过环信，登录成功后，只要没调logout方法，这个方法的返回值一直是true
        // emClient.isLoggedInBefore();

        // emClient.getCurrentUser() 行为难以预测，所以自己写一个变量控制

        return currentUserId != null;
    }

    /**
     * 异步注册环信，只用于测试
     */
    public void asyncRegister(@NonNull final String hxId, @NonNull final String password){
        Runnable runnable = new Runnable() {
            @Override public void run() {
                try {
                    emClient.createAccount(hxId, password);
                    Timber.d("RegisterHx success: %s", hxId);
                    eventBus.post(new HxSimpleEvent(HxEventType.REGISTER));

                } catch (HyphenateException e) {
                    Timber.e(e, "RegisterHx fail");
                    eventBus.post(new HxErrorEvent(HxEventType.REGISTER, e));
                }
            }
        };

        executorService.submit(runnable);
    }

    /**
     * 异步登录环信
     */
    public void asyncLogin(@NonNull final String hxId, @NonNull final String password){
        emClient.login(hxId, password, new EMCallBack() {
            @Override public void onSuccess() {
                Timber.d("%s login success", hxId);
                currentUserId = hxId;
                eventBus.post(new HxSimpleEvent(HxEventType.LOGIN));
            }

            @Override public void onError(int code, String message) {
                Timber.d("%s login error, code is %s.", hxId, code);
                eventBus.post(new HxErrorEvent(HxEventType.LOGIN, code, message));
            }

            /* no-op */
            @Override public void onProgress(int progress, String status) {
            }
        });
    }

    public void asyncLogout(){
        Runnable runnable = new Runnable() {
            @Override public void run() {
                currentUserId = null;
                emClient.logout(true);
                HxContactManager.getInstance().reset();
            }
        };

        executorService.submit(runnable);
    }
}
