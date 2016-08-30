package com.feicuiedu.readgroup.user.user;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.feicuiedu.readgroup.R;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.soundcloud.android.crop.Crop;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class UserFragment extends Fragment implements UserView{

    private static final int REQUEST_CODE_LOCAL = 3;

    private Unbinder unbinder;

    @BindView(R.id.image_avatar) ImageView ivAvatar;
    @BindView(R.id.text_name) TextView tvName;
    @BindView(R.id.progress_bar) ProgressBar progressBar;

    private UserPresenter userPresenter;
    private File avatarCache;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPresenter = new UserPresenter();
        userPresenter.onCreate();
        avatarCache = new File(getContext().getFilesDir() + File.separator + "avatar.jpeg");
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        userPresenter.attachView(this);
        userPresenter.updateUser();

    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        userPresenter.detachView();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        userPresenter.onDestroy();
    }

    @OnClick(R.id.image_avatar)
    public void selectPicFromLocal() {
        Intent intent =  new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_LOCAL) {
            Uri selectedImage = data.getData();
            Crop.of(selectedImage, Uri.fromFile(avatarCache))
                    .asSquare()
                    .withMaxSize(300, 300)
                    .start(getContext(), this);
        } else if (resultCode == Activity.RESULT_OK && requestCode == Crop.REQUEST_CROP) {
            userPresenter.updateAvatar(avatarCache);
        }
    }

    // start-interface: UserView
    @Override public void refreshUser(String hxId) {
        EaseUserUtils.setUserAvatar(getContext(), hxId, ivAvatar);
        EaseUserUtils.setUserNick(hxId, tvName);
    }

    @Override public void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override public void stopLoading() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override public void showUpdateAvatarFail(String msg) {
        String info = getString(R.string.user_error_update_avatar, msg);
        Toast.makeText(getContext(), info, Toast.LENGTH_SHORT).show();
    } // end-interface: UserView
}
