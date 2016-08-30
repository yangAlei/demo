package com.feicuiedu.readgroup.network;


import com.feicuiedu.readgroup.network.entity.FileResult;
import com.feicuiedu.readgroup.network.entity.LoginResult;
import com.feicuiedu.readgroup.network.entity.RegisterResult;
import com.feicuiedu.readgroup.network.event.LoginEvent;
import com.feicuiedu.readgroup.network.event.RegisterEvent;
import com.feicuiedu.readgroup.network.event.UpdateUserEvent;
import com.feicuiedu.readgroup.network.event.UploadFileEvent;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import timber.log.Timber;

public class BombClient implements BombConst {

    private static BombClient sInstance;

    public static BombClient getInstance() {
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

    public Call getSearchUserCall(String query) {
        String url = String.format(SEARCH_USER_URL, query);
        Request request = new Request.Builder()
                .url(url)
                .build();
        return okHttpClient.newCall(request);
    }

    public Call getGetUsersCall(List<String> ids) {
        String url = String.format(GET_USERS_URL, gson.toJson(ids));
        Request request = new Request.Builder()
                .url(url)
                .build();
        return okHttpClient.newCall(request);
    }

    @SuppressWarnings("WeakerAccess")
    public Call getUpdateUserCall(String id, String avatar) {
        String url = String.format(UPDATE_USER_URL, id);

        String content = "{\"avatar\":\"%s\"}";
        content = String.format(content, avatar);
        RequestBody body = RequestBody.create(MediaType.parse(CONTENT_TYPE_JSON), content);

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        return okHttpClient.newCall(request);
    }

    @SuppressWarnings("WeakerAccess")
    public Call getUploadFileCall(File file) {
        RequestBody body = RequestBody.create(MediaType.parse("image/jpeg"), file);
        Request request = new Request.Builder()
                .url(UPLOAD_FILE_URL)
                .post(body)
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

            @Override public void onResponse(Call call, Response response) {
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

                    LoginResult loginResult = handleSuccessResponse(response, LoginResult.class);

                    BombInterceptor.setToken(loginResult.getSessionToken());

                    LoginEvent event = new LoginEvent(true, null, loginResult);
                    eventBus.post(event);
                } catch (IOException e) {
                    onFailure(call, e);
                }
            }
        });
    }

    public void asyncUpdateUser(String id, final String avatar) {
        Call call = getUpdateUserCall(id, avatar);

        call.enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                UpdateUserEvent event = new UpdateUserEvent(false, e.getMessage(), null);
                eventBus.post(event);
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        handleFailedResponse(response);
                    }

                    UpdateUserEvent event = new UpdateUserEvent(true, null, avatar);
                    eventBus.post(event);
                } catch (IOException e) {
                    onFailure(call, e);
                }
            }
        });

    }

    public void asyncUploadFile(File file) {
        Call call = getUploadFileCall(file);

        call.enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                UploadFileEvent event = new UploadFileEvent(false, e.getMessage(), null);
                eventBus.post(event);
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        handleFailedResponse(response);
                    }

                    FileResult fileResult = handleSuccessResponse(response, FileResult.class);
                    UploadFileEvent event = new UploadFileEvent(true, null, fileResult.getUrl());
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

    private <T> T handleSuccessResponse(Response response, Class<T> clazz) throws IOException {
        String content = response.body().string();
        return gson.fromJson(content, clazz);
    }


}
