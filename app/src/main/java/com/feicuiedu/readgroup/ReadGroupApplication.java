package com.feicuiedu.readgroup;


import com.feicuiedu.apphx.HxBaseApplication;
import com.feicuiedu.apphx.HxModuleInitializer;
import com.feicuiedu.apphx.model.repository.DefaultLocalInviteRepo;
import com.feicuiedu.apphx.model.repository.DefaultLocalUsersRepo;

public class ReadGroupApplication extends HxBaseApplication{


    @Override public void initHxModule(HxModuleInitializer initializer) {
        initializer.setRemoteUsersRepo(new RemoteUsersRepo())
                .setLocalUsersRepo(DefaultLocalUsersRepo.getInstance(this))
                .setLocalInviteRepo(DefaultLocalInviteRepo.getInstance(this))
                .init();
    }
}
