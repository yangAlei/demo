package com.feicuiedu.readgroup.user.login;


import com.feicuiedu.apphx.basemvp.MvpView;

public interface LoginView extends MvpView {

    // 开始加载
    void startLoading();

    // 停止加载
    void stopLoading();

    // 显示登录失败提示
    void showLoginFail(String msg);

    // 导航到主页面
    void navigateToHome();

    LoginView NULL = new LoginView() {
        @Override public void startLoading() {
        }

        @Override public void stopLoading() {
        }

        @Override public void showLoginFail(String msg) {
        }

        @Override public void navigateToHome() {
        }
    };
}
