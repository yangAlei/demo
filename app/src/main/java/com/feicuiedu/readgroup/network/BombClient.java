package com.feicuiedu.readgroup.network;


import com.feicuiedu.readgroup.network.entity.BookEntity;
import com.feicuiedu.readgroup.network.entity.BookInfoResult;
import com.feicuiedu.readgroup.network.entity.BookResult;
import com.feicuiedu.readgroup.network.entity.FileResult;
import com.feicuiedu.readgroup.network.entity.LikeResult;
import com.feicuiedu.readgroup.network.entity.LoginResult;
import com.feicuiedu.readgroup.network.entity.RegisterResult;
import com.feicuiedu.readgroup.network.entity.UserLikesResult;
import com.feicuiedu.readgroup.network.event.ChangeLikeEvent;
import com.feicuiedu.readgroup.network.event.GetBookInfoEvent;
import com.feicuiedu.readgroup.network.event.GetBooksEvent;
import com.feicuiedu.readgroup.network.event.LoginEvent;
import com.feicuiedu.readgroup.network.event.RegisterEvent;
import com.feicuiedu.readgroup.network.event.UpdateUserEvent;
import com.feicuiedu.readgroup.network.event.UploadFileEvent;
import com.feicuiedu.readgroup.network.event.UserLikeEvent;
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
import okhttp3.logging.HttpLoggingInterceptor;
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

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override public void log(String message) {
                Timber.d(message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new BombInterceptor())
                .addNetworkInterceptor(loggingInterceptor)
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


    public Call getBooksCall() {
        String url = String.format(BOOKS_URL, System.currentTimeMillis());

        Request request = new Request.Builder()
                .url(url)
                .build();
        return okHttpClient.newCall(request);
    }

    public Call getBookInfoCall(String objectId) {
        String url = String.format(BOOK_INFO_URL, objectId, System.currentTimeMillis());

        Request request = new Request.Builder()
                .url(url)
                .build();
        return okHttpClient.newCall(request);
    }

    public Call getBookLikeCall(String bookId, String userId, boolean isLike) {
        String action = isLike ? "like" : "dislike";

        String url = String.format(BOOK_LIKE_URL, bookId, userId, action);
        Request request = new Request.Builder()
                .url(url)
                .build();
        return okHttpClient.newCall(request);
    }

    public Call getUserLikesCall(String userId) {
        String url = String.format(USER_LIKES_URL, userId, System.currentTimeMillis());
        Request request = new Request.Builder()
                .url(url)
                .build();
        return okHttpClient.newCall(request);
    }

    public void asyncGetBooks() {
        Call call = getBooksCall();

        call.enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                GetBooksEvent event = new GetBooksEvent(false, e.getMessage(), null);
                eventBus.post(event);
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        handleFailedResponse(response);
                    }

                    BookResult bookResult = handleSuccessResponse(response, BookResult.class);

                    GetBooksEvent event = new GetBooksEvent(bookResult.isSuccess(), bookResult.getError(), bookResult.getData());
                    eventBus.post(event);
                } catch (IOException e) {
                    onFailure(call, e);
                }
            }
        });
    }

    public void asyncGetBookInfo(String bookId) {
        Call call = getBookInfoCall(bookId);

        call.enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                GetBookInfoEvent event = new GetBookInfoEvent(e.getMessage());
                eventBus.post(event);
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        handleFailedResponse(response);
                    }

                    BookInfoResult result = handleSuccessResponse(response, BookInfoResult.class);

                    GetBookInfoEvent event = new GetBookInfoEvent(result.getData().getLikes(), result.getData().getBook());
                    eventBus.post(event);
                } catch (IOException e) {
                    onFailure(call, e);
                }
            }
        });
    }

    public void asyncChangeLike(final boolean isLike, final BookEntity bookEntity, String userId) {
        Call call = getBookLikeCall(bookEntity.getObjectId(), userId, isLike);

        call.enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                ChangeLikeEvent event = new ChangeLikeEvent(isLike, e.getMessage());
                eventBus.post(event);
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        handleFailedResponse(response);
                    }

                    LikeResult result = handleSuccessResponse(response, LikeResult.class);


                    ChangeLikeEvent event;
                    if (result.isSuccess()) {
                        event = new ChangeLikeEvent(isLike, bookEntity);
                    } else {
                        event = new ChangeLikeEvent(isLike, result.getError());
                    }

                    eventBus.post(event);
                } catch (IOException e) {
                    onFailure(call, e);
                }
            }
        });

    }

    public void asyncGetUserLikes(String userId) {
        Call call = getUserLikesCall(userId);

        call.enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                UserLikeEvent event = new UserLikeEvent(null, e.getMessage(), false);
                eventBus.post(event);
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        handleFailedResponse(response);
                    }

                    UserLikesResult result = handleSuccessResponse(response, UserLikesResult.class);

                    UserLikeEvent event = new UserLikeEvent(result.getData(), result.getError(), result.isSuccess());
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
