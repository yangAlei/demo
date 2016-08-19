package com.feicuiedu.readgroup;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.feicuiedu.apphx.model.HxUserManager;
import com.feicuiedu.readgroup.user.login.LoginFragment;
import com.feicuiedu.readgroup.user.register.RegisterFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashActivity extends AppCompatActivity {

    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (HxUserManager.getInstance().isLogin()) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_splash);
    }

    @Override public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_login)
    public void showLoginDialog() {
        if (loginFragment == null) {
            loginFragment = new LoginFragment();
        }
        loginFragment.show(getSupportFragmentManager(), null);
    }

    @OnClick(R.id.button_register)
    public void showRegisterDialog() {
        if (registerFragment == null) {
            registerFragment = new RegisterFragment();
        }
        registerFragment.show(getSupportFragmentManager(), null);
    }
}
