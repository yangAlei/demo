package com.feicuiedu.apphx.model.repository;

import com.hyphenate.easeui.domain.EaseUser;

import java.util.List;

/**
 * 远程用户仓库，代表到应用服务器获取用户数据的相关操作
 */
public interface IRemoteUsersRepo {

    /**
     * 通过用户名查询用户
     */
    List<EaseUser> queryByName(String username) throws Exception;


}
