package com.feicuiedu.readgroup.network;


import com.feicuiedu.readgroup.network.entity.UserEntity;
import com.feicuiedu.readgroup.network.entity.RegisterResult;
import com.feicuiedu.readgroup.network.event.LoginEvent;
import com.feicuiedu.readgroup.network.event.RegisterEvent;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

public class BombClient implements BombConst{

    private static BombClient sInstance;

    public static BombClient getInstance(){
        if (sInstance == null) {
            sInstance = new BombClient();
        }
        return sInstance;
    }


    private final OkHttpClient okHttpClient;
    private final EventBus eventBus;
    private final Gson gson;

    private BombClient() {
        eventBus = EventBus.getDefault();
        gson = new Gson();
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new BombInterceptor())
                .build();
    }

    public Call getRegisterCall(String username, String password) {

        String url = String.format(REGISTER_URL, username, password);

        Request request = new Request.Builder()
                .url(url)
                .build();
        return okHttpClient.newCall(request);
    }

    public Call getLoginCall(String username, String password) {
        String url = String.format(LOGIN_URL, username, password);

        Request request = new Request.Builder()
                .url(url)
                .build();
        return okHttpClient.newCall(request);
    }

    public Call getSearchUserCall(String query){
        String url = String.format(SEARCH_USER_URL, query);
        Request request = new Request.Builder()
                .url(url)
                .build();
        return okHttpClient.newCall(request);
    }

    public void asyncRegister(String username, String password) {
        Call call = getRegisterCall(username, password);

        call.enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                RegisterEvent event = new RegisterEvent(false, e.getMessage());
                eventBus.post(event);
            }

            @Override public void onResponse(Call call, Response response){
                try {
                    if (!response.isSuccessful()) {
                        handleFailedResponse(response);
                    }

                    RegisterResult registerResult = handleSuccessResponse(response, RegisterResult.class);

                    RegisterEvent event = new RegisterEvent(registerResult.isSuccess(), registerResult.getError());
                    eventBus.post(event);
                } catch (IOException e) {
                    onFailure(call, e);
                }
            }
        });
    }

    public void asyncLogin(String username, String password) {
        Call call = getLoginCall(username, password);

        call.enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                LoginEvent event = new LoginEvent(false, e.getMessage(), null);
                eventBus.post(event);
            }

            @Override public void onResponse(Call call, Response response) {
                try {
                    if (!response.isSuccessful()) {
                        handleFailedResponse(response);
                    }

                    UserEntity userEntity = handleSuccessResponse(response, UserEntity.class);
                    LoginEvent event = new LoginEvent(true, null, userEntity);
                    eventBus.post(event);
                } catch (IOException e) {
                    onFailure(call, e);
                }
            }
        });
    }

    private void handleFailedResponse(Response response) throws IOException {
        String error = response.body().string();
        Timber.e(error);
        throw new IOException(response.code() + ", " + error);
    }

    private<T> T handleSuccessResponse(Response response, Class<T> clazz) throws IOException {
        String content = response.body().string();
        return gson.fromJson(content, clazz);
    }


}
