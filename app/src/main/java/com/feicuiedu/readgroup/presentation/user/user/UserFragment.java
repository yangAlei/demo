package com.feicuiedu.readgroup.presentation.user.user;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.feicuiedu.apphx.model.HxUserManager;
import com.feicuiedu.readgroup.R;
import com.feicuiedu.readgroup.network.entity.BookEntity;
import com.feicuiedu.readgroup.presentation.books.bookinfo.BookInfoActivity;
import com.feicuiedu.readgroup.presentation.books.booklist.BooksAdapter;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.List;

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
    @BindView(R.id.list_book) ListView listView;
    @BindView(R.id.layout_refresh) SwipeRefreshLayout refreshLayout;

    private UserPresenter userPresenter;
    private BooksAdapter booksAdapter;
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

        booksAdapter = new BooksAdapter();
        listView.setAdapter(booksAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BookEntity bookEntity = booksAdapter.getItem(position);
                Intent intent = BookInfoActivity.getStartIntent(getContext(), bookEntity);
                startActivity(intent);
            }
        });

        refreshLayout.setColorSchemeResources(R.color.brown);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                userPresenter.getLikes(true);
            }
        });

        userPresenter.attachView(this);
        userPresenter.updateUser();

        refreshLayout.postDelayed(new Runnable() {
            @Override public void run() {
                userPresenter.getLikes(false);
            }
        }, 100);

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

    @OnClick(R.id.button_logout)
    public void logout(){
        // 登出时清空通知
        EaseUI.getInstance()
                .getNotifier()
                .reset();
        HxUserManager.getInstance().asyncLogout();
        getActivity().finish();
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
    }

    @Override public void setBooks(List<BookEntity> books) {
        booksAdapter.setBooks(books);
    }

    @Override public void setRefreshing(boolean refreshing) {
        refreshLayout.setRefreshing(refreshing);
    }

    @Override public void showRefreshFail(String msg) {
        String info = getString(R.string.user_error_refresh_likes, msg);
        Toast.makeText(getContext(), info, Toast.LENGTH_SHORT).show();
    } // end-interface: UserView
}
