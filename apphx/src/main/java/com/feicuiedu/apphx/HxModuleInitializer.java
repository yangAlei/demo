package com.feicuiedu.apphx;


import com.feicuiedu.apphx.model.HxContactManager;
import com.feicuiedu.apphx.model.HxUserManager;
import com.feicuiedu.apphx.model.repository.ILocalInviteRepo;
import com.feicuiedu.apphx.model.repository.ILocalUsersRepo;
import com.feicuiedu.apphx.model.repository.IRemoteUsersRepo;

public class HxModuleInitializer {

    private static HxModuleInitializer sInstance;

    public static HxModuleInitializer getInstance() {
        if (sInstance == null) {
            sInstance = new HxModuleInitializer();
        }
        return sInstance;
    }

    private HxModuleInitializer() {
    }

    private IRemoteUsersRepo remoteUsersRepo;

    private ILocalUsersRepo localUsersRepo;

    private ILocalInviteRepo localInviteMessageRepo;

    public HxModuleInitializer setRemoteUsersRepo(IRemoteUsersRepo remoteUsersRepo) {
        this.remoteUsersRepo = remoteUsersRepo;
        return this;
    }

    public HxModuleInitializer setLocalUsersRepo(ILocalUsersRepo localUsersRepo) {
        this.localUsersRepo = localUsersRepo;
        return this;
    }

    public HxModuleInitializer setLocalInviteRepo(ILocalInviteRepo localInviteMessageRepo) {
        this.localInviteMessageRepo = localInviteMessageRepo;
        return this;
    }

    public void init() {

        if (remoteUsersRepo == null) {
            throw new RuntimeException("Must set remoteUsersRepo before init!");
        }

        if (localUsersRepo == null) {
            throw new RuntimeException("Must set localUsersRepo before init!");
        }

        HxUserManager.getInstance();

        HxContactManager.getInstance()
                .initLocalUsersRepo(localUsersRepo)
                .initRemoteUsersRepo(remoteUsersRepo)
                .initLocalInviteRepo(localInviteMessageRepo);
    }
}
