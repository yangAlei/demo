package com.feicuiedu.readgroup;

import com.feicuiedu.readgroup.network.BombClient;
import com.feicuiedu.readgroup.network.entity.RegisterResult;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

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

        Call call = bombClient.getRegisterCall("yc", "123");
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
}
