package com.feicuiedu.readgroup.presentation.books.booklist;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.feicuiedu.readgroup.R;
import com.feicuiedu.readgroup.network.entity.BookEntity;
import com.feicuiedu.readgroup.presentation.books.bookinfo.BookInfoActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BooksFragment extends Fragment implements BooksView{

    private Unbinder unbinder;

    @BindView(R.id.list_book) ListView listView;
    @BindView(R.id.layout_refresh) SwipeRefreshLayout refreshLayout;

    private BooksAdapter booksAdapter;
    private BooksPresenter booksPresenter;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        booksPresenter = new BooksPresenter();
        booksPresenter.onCreate();
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_books, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        booksPresenter.attachView(this);

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
                booksPresenter.getBooks(true);
            }
        });
    }

    @Override public void onResume() {
        super.onResume();
        booksPresenter.getBooks(false);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        booksPresenter.detachView();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        booksPresenter.onDestroy();
    }

    // start-interface: BooksView
    @Override public void setBooks(List<BookEntity> books) {
        booksAdapter.setBooks(books);
    }

    @Override public void setRefreshing(boolean refreshing) {
        refreshLayout.setRefreshing(refreshing);
    }

    @Override public void showRefreshFail(String msg) {
        String info = getString(R.string.book_error_refreshing_fail, msg);
        Toast.makeText(getContext(), info, Toast.LENGTH_SHORT).show();
    } // end-interface: BooksView
}
