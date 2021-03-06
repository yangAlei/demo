package com.feicuiedu.readgroup;

import com.feicuiedu.readgroup.hx.RemoteUsersRepo;
import com.feicuiedu.readgroup.network.BombClient;
import com.feicuiedu.readgroup.network.entity.BookEntity;
import com.feicuiedu.readgroup.network.entity.BookInfoResult;
import com.feicuiedu.readgroup.network.entity.BookResult;
import com.feicuiedu.readgroup.network.entity.GetUsersResult;
import com.feicuiedu.readgroup.network.entity.RegisterResult;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;
import timber.log.Timber;

import static org.junit.Assert.*;


public class BombClientTest {

    private BombClient bombClient;
    private Gson gson;

    @BeforeClass
    public static void initTimber(){
        Timber.plant(new Timber.Tree() {
            @Override protected void log(int priority, String tag, String message, Throwable t) {
                System.out.println( message);
            }
        });
    }

    @Before
    public void init(){
        bombClient = BombClient.getInstance();
        gson = new Gson();
    }

    @Test
    public void register() {

        Call call = bombClient.getRegisterCall("test1", "123");
        try {
            Response response = call.execute();
            Timber.d("code: %d",  response.code());

            String content = response.body().string();
            Timber.d(content);

            RegisterResult registerResult = gson.fromJson(content, RegisterResult.class);
            Timber.d(registerResult.getError());

            if (registerResult.isSuccess()) {
                assertNotNull(registerResult.getData());
            } else {
                assertNotNull(registerResult.getError());
            }

        } catch (Exception e) {
            Timber.e(e, "getRegisterCall");
        }

    }

    @Test
    public void login() throws IOException{
        Response response = bombClient.getLoginCall("ycj", "123")
                .execute();
        Timber.d(response.body().string());
    }

    @Test
    public void searchUser() throws Exception {
        new RemoteUsersRepo().queryByName("yc");
    }

    @Test
    public void getUsers() throws Exception {
        ArrayList<String> ids = new ArrayList<>();
        ids.add("0bf45919f4");
        ids.add("2569ef38dc");
        Call call = bombClient.getGetUsersCall(ids);

        Response response = call.execute();
        GetUsersResult result = gson.fromJson(response.body().string(), GetUsersResult.class);

        Timber.d(result.getData().get(0).getObjectId());
    }

    @Test
    public void getBooks() throws Exception {
        Call call = bombClient.getBooksCall();

        Response response = call.execute();

        String content = response.body().string();

        Timber.d(content);

        BookResult bookResult = gson.fromJson(content, BookResult.class);

        for (BookEntity bookEntity : bookResult.getData()) {
            Timber.d(bookEntity == null? "null" : bookEntity.toString());
        }
    }

    @Test
    public void getBookInfo() throws Exception {
        Call call = bombClient.getBookInfoCall("677ab0ba0f");

        Response response = call.execute();
        String content = response.body().string();
        BookInfoResult bookInfoResult = gson.fromJson(content, BookInfoResult.class);

        Timber.d(bookInfoResult.getData().getBook().toString());
    }

    @Test
    public void likeBook() throws Exception {
        Call call = bombClient.getBookLikeCall("677ab0ba0f", "0bf45919f4", true);
        Response response = call.execute();
        Timber.d(response.body().string());
    }

    @Test
    public void getUserLikes() throws Exception {
        Call call = bombClient.getUserLikesCall("0bf45919f4");
        Response response = call.execute();
        Timber.d(response.body().string());
    }

}
