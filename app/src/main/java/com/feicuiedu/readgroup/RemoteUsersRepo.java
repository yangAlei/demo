package com.feicuiedu.readgroup;


import com.feicuiedu.apphx.model.repository.IRemoteUsersRepo;
import com.feicuiedu.readgroup.network.BombClient;
import com.feicuiedu.readgroup.network.entity.SearchUserResult;
import com.feicuiedu.readgroup.network.entity.UserEntity;
import com.google.gson.Gson;
import com.hyphenate.easeui.domain.EaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;
import timber.log.Timber;

class RemoteUsersRepo implements IRemoteUsersRepo{

    private final BombClient bombClient;
    private final Gson gson;

    public RemoteUsersRepo(){
        bombClient = BombClient.getInstance();
        gson = new Gson();
    }


    @Override public List<EaseUser> queryByName(String query) throws Exception {

        Call call = bombClient.getSearchUserCall(query);

        Response response = call.execute();

        if (!response.isSuccessful()) {
            throw new Exception(response.body().string());
        }

        String content = response.body().string();

        Timber.d(content);
        SearchUserResult result = gson.fromJson(content, SearchUserResult.class);

        if (!result.isSuccess()) {
            throw new Exception(result.getError());
        }


        return convertAll(result.getData());
    }

    private List<EaseUser> convertAll(List<UserEntity> users) {

        if (users == null) {
            return Collections.emptyList();
        }

        ArrayList<EaseUser> easeUsers = new ArrayList<>();
        for (UserEntity userEntity : users) {
            easeUsers.add(convert(userEntity));
        }
        return easeUsers;
    }

    private EaseUser convert(UserEntity userEntity){
        EaseUser easeUser = new EaseUser(userEntity.getObjectId());
        easeUser.setNick(userEntity.getUsername());
        return easeUser;
    }
}
