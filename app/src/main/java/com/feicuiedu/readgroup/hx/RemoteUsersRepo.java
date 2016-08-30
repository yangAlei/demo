package com.feicuiedu.readgroup.hx;


import com.feicuiedu.apphx.model.repository.IRemoteUsersRepo;
import com.feicuiedu.readgroup.network.BombClient;
import com.feicuiedu.readgroup.network.entity.GetUsersResult;
import com.feicuiedu.readgroup.network.entity.SearchUserResult;
import com.google.gson.Gson;
import com.hyphenate.easeui.domain.EaseUser;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;
import timber.log.Timber;

public class RemoteUsersRepo implements IRemoteUsersRepo{

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


        return UserConverter.convertAll(result.getData());
    }

    @Override public List<EaseUser> getUsers(List<String> ids) throws Exception {

        Call call = bombClient.getGetUsersCall(ids);

        Response response = call.execute();

        if (!response.isSuccessful()) {
            throw new Exception(response.body().string());
        }

        String content = response.body().string();

        Timber.d(content);
        GetUsersResult result = gson.fromJson(content, GetUsersResult.class);

        if (!result.isSuccess()) {
            throw new Exception(result.getError());
        }


        return UserConverter.convertAll(result.getData());
    }
}
