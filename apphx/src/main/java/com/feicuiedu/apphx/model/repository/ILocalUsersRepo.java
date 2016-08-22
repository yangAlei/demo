package com.feicuiedu.apphx.model.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hyphenate.easeui.domain.EaseUser;

import java.util.List;

/**
 * 本地用户数据存储接口
 */
public interface ILocalUsersRepo {

    void save(@NonNull List<EaseUser> userList);

    @Nullable EaseUser getUser(String hxId);
}
