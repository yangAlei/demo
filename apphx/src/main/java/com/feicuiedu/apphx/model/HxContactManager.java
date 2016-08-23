package com.feicuiedu.apphx.model;

import com.feicuiedu.apphx.model.entity.InviteMessage;
import com.feicuiedu.apphx.model.event.HxErrorEvent;
import com.feicuiedu.apphx.model.event.HxEventType;
import com.feicuiedu.apphx.model.event.HxRefreshContactEvent;
import com.feicuiedu.apphx.model.event.HxRefreshInviteEvent;
import com.feicuiedu.apphx.model.event.HxSearchContactEvent;
import com.feicuiedu.apphx.model.event.HxSimpleEvent;
import com.feicuiedu.apphx.model.repository.ILocalInviteRepo;
import com.feicuiedu.apphx.model.repository.ILocalUsersRepo;
import com.feicuiedu.apphx.model.repository.IRemoteUsersRepo;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMContactManager;
import com.hyphenate.easeui.domain.EaseUser;
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

    private ILocalUsersRepo localUsersRepo;
    private IRemoteUsersRepo remoteUsersRepo;
    private ILocalInviteRepo localInviteRepo;

    private List<String> contacts;
    private String currentUserId;

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

        localInviteRepo.delete(hxId);
        notifyInviteRefresh();
    }

    /**
     * 收到好友邀请
     * <p/>
     * A 向 B发送邀请时，B的这个方法会被调用
     *
     * @param hxId   环信ID
     * @param reason 理由
     */
    @Override public void onContactInvited(String hxId, String reason) {
        Timber.d("onContactInvited %s, reason: %s", hxId, reason);

        InviteMessage inviteMessage = new InviteMessage(hxId, currentUserId, InviteMessage.Status.RAW);
        localInviteRepo.save(inviteMessage);
        notifyInviteRefresh();
    }

    /**
     * 好友请求被同意
     * <p/>
     * B 同意 A的好友邀请时，A的这个方法会被调用
     */
    @Override public void onContactAgreed(String hxId) {
        Timber.d("onContactAgreed %s", hxId);

        InviteMessage inviteMessage = new InviteMessage(hxId, currentUserId, InviteMessage.Status.REMOTE_ACCEPTED);
        localInviteRepo.save(inviteMessage);
        notifyInviteRefresh();
    }

    /**
     * 好友请求被拒绝
     */
    /* no-op */
    @Override public void onContactRefused(String hxId) {
        Timber.d("onContactRefused %s", hxId);
    } // end-interface: EMContactListener


    public boolean isFriend(String hxId) {
        return contacts != null && contacts.contains(hxId);
    }


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

    public void sendInvite(final String hxId) {
        Runnable runnable = new Runnable() {
            @Override public void run() {
                try {
                    emContactManager.addContact(hxId, "");
                    eventBus.post(new HxSimpleEvent(HxEventType.SEND_INVITE));
                } catch (HyphenateException e) {
                    Timber.e(e, "sendInvite");
                    eventBus.post(new HxErrorEvent(HxEventType.SEND_INVITE, e));
                }
            }
        };

        executorService.submit(runnable);
    }

    public void acceptInvite(final InviteMessage invite) {
        Runnable runnable = new Runnable() {
            @Override public void run() {
                try {
                    emContactManager.acceptInvitation(invite.getFromHxId());

                    invite.setStatus(InviteMessage.Status.ACCEPTED);
                    localInviteRepo.save(invite);

                    eventBus.post(new HxSimpleEvent(HxEventType.ACCEPT_INVITE));
                } catch (HyphenateException e) {
                    Timber.e(e, "acceptInvite");
                    eventBus.post(new HxErrorEvent(HxEventType.ACCEPT_INVITE, e));
                }
            }
        };

        executorService.submit(runnable);
    }

    public void refuseInvite(final InviteMessage invite) {
        Runnable runnable = new Runnable() {
            @Override public void run() {
                try {
                    emContactManager.declineInvitation(invite.getFromHxId());

                    invite.setStatus(InviteMessage.Status.REFUSED);
                    localInviteRepo.save(invite);

                    eventBus.post(new HxSimpleEvent(HxEventType.REFUSE_INVITE));
                } catch (HyphenateException e) {
                    Timber.e(e, "declineInvite");
                    eventBus.post(new HxErrorEvent(HxEventType.REFUSE_INVITE, e));
                }
            }
        };

        executorService.submit(runnable);
    }

    public void getInvites() {
        executorService.submit(new Runnable() {
            @Override public void run() {
                notifyInviteRefresh();
            }
        });
    }


    public void searchContacts(final String username) {
        Runnable runnable = new Runnable() {
            @Override public void run() {
                try {
                    // 从应用服务器查询用户列表
                    List<EaseUser> users = remoteUsersRepo.queryByName(username);
                    // 将查询到的接口存储到本地数据仓库中
                    localUsersRepo.save(users);

                    ArrayList<String> contacts = new ArrayList<>();

                    for (EaseUser easeUser : users) {
                        contacts.add(easeUser.getUsername());
                    }

                    // 将结果发送给Presenter
                    eventBus.post(new HxSearchContactEvent(contacts));
                } catch (Exception e) {
                    Timber.e(e, "searchContacts");
                    eventBus.post(new HxSearchContactEvent(e.getMessage()));
                }
            }
        };
        executorService.submit(runnable);
    }


    @SuppressWarnings("UnusedReturnValue")
    public HxContactManager initRemoteUsersRepo(IRemoteUsersRepo remoteUsersRepo) {
        this.remoteUsersRepo = remoteUsersRepo;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public HxContactManager initLocalUsersRepo(ILocalUsersRepo localUsersRepo) {
        this.localUsersRepo = localUsersRepo;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public HxContactManager initLocalInviteRepo(ILocalInviteRepo localInviteRepo) {
        this.localInviteRepo = localInviteRepo;
        return this;
    }

    public void setCurrentUser(String hxId) {
        this.currentUserId = hxId;
        localInviteRepo.setCurrentUser(hxId);
    }

    public void reset() {
        contacts = null;
        currentUserId = null;
        localInviteRepo.setCurrentUser(null);
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

    private void notifyInviteRefresh() {
        List<InviteMessage> messages = localInviteRepo.getAll();
        eventBus.post(new HxRefreshInviteEvent(messages));
    }
}
