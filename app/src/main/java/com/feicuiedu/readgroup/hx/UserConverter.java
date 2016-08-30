package com.feicuiedu.readgroup.hx;

import com.feicuiedu.readgroup.network.entity.UserEntity;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserConverter {

    private UserConverter(){}

    public static List<EaseUser> convertAll(List<UserEntity> users) {

        if (users == null) {
            return Collections.emptyList();
        }

        ArrayList<EaseUser> easeUsers = new ArrayList<>();
        for (UserEntity userEntity : users) {
            easeUsers.add(convert(userEntity));
        }
        return easeUsers;
    }

    public static EaseUser convert(UserEntity userEntity){
        EaseUser easeUser = new EaseUser(userEntity.getObjectId());
        easeUser.setNick(userEntity.getUsername());
        easeUser.setAvatar(userEntity.getAvatar());
        EaseCommonUtils.setUserInitialLetter(easeUser);
        return easeUser;
    }
}
