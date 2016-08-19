package com.feicuiedu.readgroup.user.login;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.feicuiedu.readgroup.HomeActivity;
import com.feicuiedu.readgroup.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LoginFragment extends DialogFragment implements LoginView {


    private LoginPresenter loginPresenter;

    private Unbinder unbinder;

    @BindView(R.id.edit_username) protected EditText etUsername;
    @BindView(R.id.edit_password) protected EditText etPassword;
    @BindView(R.id.button_confirm) Button btnConfirm;
    @BindView(R.id.progress_bar) ProgressBar progressBar;


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginPresenter = new LoginPresenter();
        loginPresenter.onCreate();
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        loginPresenter.attachView(this);
    }


    @Override public void onDestroyView() {
        super.onDestroyView();
        loginPresenter.detachView();
        unbinder.unbind();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        loginPresenter.onDestroy();
    }

    // start-interface: LoginView
    @Override public void startLoading() {
        btnConfirm.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        setCancelable(false);
    }

    @Override public void stopLoading() {
        btnConfirm.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        setCancelable(true);
    }

    @Override public void showLoginFail(String msg) {
        String info = getString(R.string.user_error_login_fail, msg);
        Toast.makeText(getContext(), info, Toast.LENGTH_SHORT).show();
    }

    @Override public void navigateToHome() {
        Intent intent = new Intent(getContext(), HomeActivity.class);
        startActivity(intent);
        getActivity().finish();
    } // end-interface: LoginView

    @OnClick(R.id.button_confirm)
    public void login() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            String info = getString(R.string.user_error_not_null);
            Toast.makeText(getContext(), info, Toast.LENGTH_SHORT).show();
            return;
        }

        loginPresenter.login(username, password);
    }
}
