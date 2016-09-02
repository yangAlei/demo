package com.feicuiedu.readgroup.presentation.books.bookinfo;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.feicuiedu.readgroup.R;
import com.feicuiedu.readgroup.network.entity.BookEntity;
import com.feicuiedu.readgroup.network.entity.UserEntity;
import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookInfoActivity extends AppCompatActivity implements BookInfoView {

    private static final String EXTRA_KEY_BOOK_ENTITY = "EXTRA_KEY_BOOK_ENTITY";

    public static Intent getStartIntent(Context context, BookEntity bookEntity) {

        Gson gson = new Gson();
        Intent intent = new Intent(context, BookInfoActivity.class);
        intent.putExtra(EXTRA_KEY_BOOK_ENTITY, gson.toJson(bookEntity));
        return intent;
    }

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.layout_refresh) SwipeRefreshLayout refreshLayout;
    @BindView(R.id.list_likes) ListView listView;

    private BookEntity bookEntity;

    private BookInfoPresenter presenter;
    private BookHeaderHolder bookHeaderHolder;

    private LikesAdapter likesAdapter;

    private int menuId;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bookEntity = new Gson()
                .fromJson(getIntent().getStringExtra(EXTRA_KEY_BOOK_ENTITY), BookEntity.class);
        setContentView(R.layout.activity_book_info);

        presenter = new BookInfoPresenter();
        presenter.onCreate();
        presenter.attachView(this);

        refreshLayout.postDelayed(new Runnable() {
            @Override public void run() {
                presenter.getBookInfo(bookEntity.getObjectId(), false);
            }
        }, 100);

    }

    @SuppressWarnings("ConstantConditions")
    @Override public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        likesAdapter = new LikesAdapter(new LikesAdapter.OnInviteContactListener() {
            @Override public void onInvite(@NonNull UserEntity userEntity) {
                presenter.sendInvite(userEntity.getObjectId());
            }
        });
        listView.setAdapter(likesAdapter);

        View header = LayoutInflater.from(this)
                .inflate(R.layout.partial_header_book, listView, false);
        bookHeaderHolder = new BookHeaderHolder(header);
        listView.addHeaderView(header);

        refreshLayout.setColorSchemeResources(R.color.brown);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                presenter.getBookInfo(bookEntity.getObjectId(), true);
            }
        });
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        presenter.onDestroy();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {

        if (menuId != 0) {
            getMenuInflater().inflate(menuId, menu);
            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_like:
                presenter.changeLike(bookEntity, true);
                break;
            case R.id.menu_dislike:
                presenter.changeLike(bookEntity, false);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    // start-interface: BookInfoView
    @SuppressWarnings("ConstantConditions")
    @Override public void showBookInfo(BookEntity bookEntity, List<UserEntity> likes) {
        getSupportActionBar().setTitle(bookEntity.getTitle());

        bookHeaderHolder.bind(bookEntity);

        likesAdapter.setData(likes);
    }

    @Override public void setRefreshing(boolean refreshing) {
        refreshLayout.setRefreshing(refreshing);
    }

    @Override public void showGetBookInfoFail(String msg) {
        String info = getString(R.string.book_error_refresh_book_info, msg);
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
    }

    @Override public void toggleLike(boolean showLike) {

        menuId = showLike ? R.menu.activity_menu_info_like : R.menu.activity_menu_info_dislike;

        invalidateOptionsMenu();
    }

    @Override public void showLikeActionSuccess(boolean isLike) {
        int msgId = isLike ? R.string.book_like_success : R.string.book_dislike_success;
        Toast.makeText(this, msgId, Toast.LENGTH_SHORT).show();
    }

    @Override public void showLikeActionFail(boolean isLike, String msg) {
        int strId = isLike ? R.string.book_error_like : R.string.book_error_dislike;
        String info = getString(strId, msg);
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
    }

    @Override public void showSendInviteResult(boolean success) {
        if (success) {
            Toast.makeText(this, com.feicuiedu.apphx.R.string.hx_contact_send_invite_success, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, com.feicuiedu.apphx.R.string.hx_contact_send_invite_fail, Toast.LENGTH_SHORT).show();
        }
    } // end-interface: BookInfoView

}
