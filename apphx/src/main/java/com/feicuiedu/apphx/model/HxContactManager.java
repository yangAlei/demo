package com.feicuiedu.apphx.model;

import com.feicuiedu.apphx.model.event.HxErrorEvent;
import com.feicuiedu.apphx.model.event.HxEventType;
import com.feicuiedu.apphx.model.event.HxRefreshContactEvent;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMContactManager;
import com.hyphenate.exceptions.HyphenateException;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

/**
 * 环信联系人管理类
 */
public class HxContactManager implements EMConnectionListener, EMContactListener {

    private static HxContactManager sInstance;

    public static HxContactManager getInstance() {
        if (sInstance == null) {
            sInstance = new HxContactManager();
        }
        return sInstance;
    }

    private final ExecutorService executorService;
    private final EMContactManager emContactManager;
    private final EventBus eventBus;

    private List<String> contacts;

    private HxContactManager() {
        executorService = Executors.newSingleThreadExecutor();

        EMClient emClient = EMClient.getInstance();
        emClient.addConnectionListener(this);

        emContactManager = emClient.contactManager();
        emContactManager.setContactListener(this);

        eventBus = EventBus.getDefault();
    }

    // start-interface: EMConnectionListener
    @Override public void onConnected() {
        if (contacts == null) {
            asyncGetContactsFromServer();
        }
    }

    /* no-op */
    @Override public void onDisconnected(int i) {
    } // end-interface: EMConnectionListener


    // start-interface: EMContactListener

    /**
     * 添加联系人。
     */
    @Override public void onContactAdded(String hxId) {
        Timber.d("onContactAdded %s", hxId);
        if (contacts == null) {
            asyncGetContactsFromServer();
        } else {
            contacts.add(hxId);
            notifyContactsRefresh();
        }
    }

    /**
     * 删除联系人。
     */
    @Override public void onContactDeleted(String hxId) {
        Timber.d("onContactDeleted %s", hxId);
        if (contacts == null) {
            asyncGetContactsFromServer();
        } else {
            contacts.remove(hxId);
            notifyContactsRefresh();
        }
    }

    /**
     * 收到好友邀请
     *
     * @param hxId   环信ID
     * @param reason 理由
     */
    @Override public void onContactInvited(String hxId, String reason) {
        Timber.d("onContactInvited %s, reason: %s", hxId, reason);
    }

    /**
     * 好友请求被同意
     */
    @Override public void onContactAgreed(String hxId) {
        Timber.d("onContactAgreed %s", hxId);
    }

    /**
     * 好友请求被拒绝
     */
    /* no-op */
    @Override public void onContactRefused(String hxId) {
        Timber.d("onContactRefused %s", hxId);
    } // end-interface: EMContactListener

    public void getContacts() {
        if (contacts != null) {
            notifyContactsRefresh();
        } else {
            asyncGetContactsFromServer();
        }
    }


    /**
     * 删除联系人，如果删除成功，会自动触发{@link #onContactDeleted(String)}
     * <p/>
     * 注意：A把B删除了，B客户端的{@link #onContactDeleted(String)}也会触发。
     *
     * @param hxId 对方的环信Id
     */
    public void deleteContact(final String hxId) {
        Runnable runnable = new Runnable() {
            @Override public void run() {
                try {
                    emContactManager.deleteContact(hxId);
                } catch (HyphenateException e) {
                    Timber.e(e, "deleteContact");
                    // 删除失败
                    eventBus.post(new HxErrorEvent(HxEventType.DELETE_CONTACT, e));
                }
            }
        };

        executorService.submit(runnable);
    }

    public void reset() {
        contacts = null;
    }

    private void asyncGetContactsFromServer() {
        Runnable runnable = new Runnable() {
            @Override public void run() {
                try {
                    contacts = emContactManager.getAllContactsFromServer();
                    notifyContactsRefresh();
                } catch (HyphenateException e) {
                    Timber.e(e, "asyncGetContactsFromServer");
                }
            }
        };

        executorService.submit(runnable);
    }

    private void notifyContactsRefresh() {
        List<String> currentContacts;

        if (contacts == null) {
            currentContacts = Collections.emptyList();
        } else {
            currentContacts = new ArrayList<>(contacts);
        }

        eventBus.post(new HxRefreshContactEvent(currentContacts));
    }
}
