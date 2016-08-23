package com.feicuiedu.apphx.presentation.contact.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.feicuiedu.apphx.R;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.widget.EaseContactList;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * 搜索联系人页面
 */
public class HxSearchContactActivity extends AppCompatActivity
        implements HxSearchContactView, MenuItemCompat.OnActionExpandListener {

    private ProgressBar progressBar;
    private EaseContactList easeContactList;
    private ListView listView;
    private List<EaseUser> contactList;
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private ImageView imageView;

    private SearchRecentSuggestions suggestions;

    private HxSearchContactPresenter presenter;

    private String selectedHxId;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hx_search_contact);

        presenter = new HxSearchContactPresenter();
        presenter.onCreate();
        presenter.attachView(this);

        handleIntent(getIntent());
    }

    @Override public void onContentChanged() {
        super.onContentChanged();

        suggestions = new SearchRecentSuggestions(this,
                HxSearchContactProvider.AUTHORITY, HxSearchContactProvider.MODE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // 初始化联系人列表
        easeContactList = (EaseContactList) findViewById(R.id.ease_contact_list);
        //noinspection ConstantConditions
        listView = easeContactList.getListView();
        contactList = new ArrayList<>();
        easeContactList.init(contactList);
        registerForContextMenu(listView);

        imageView = (ImageView) findViewById(R.id.image_add_contact);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                MenuItemCompat.expandActionView(searchMenuItem);
            }
        });
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_hx_search_contact, menu);

        searchMenuItem = menu.findItem(R.id.menu_search);
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, this);

        searchView = (SearchView) searchMenuItem.getActionView();
        initSearchView(searchView);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_clear_history) {
            suggestions.clearHistory();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v == listView) {
            int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
            Timber.d("onCreateContextMenu %d", position);

            EaseUser easeUser = (EaseUser) listView.getItemAtPosition(position);

            if (easeUser == null) return; // 长按HeaderView的情况

            selectedHxId = easeUser.getUsername();
            getMenuInflater().inflate(R.menu.activity_hx_search_contact_list, menu);
        }
    }

    @Override public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_add_contact) {
            searchView.clearFocus();
            presenter.sendInvite(selectedHxId);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        presenter.onDestroy();
    }

    // start-interface: MenuItemCompat.OnActionExpandListener
    @Override public boolean onMenuItemActionExpand(MenuItem item) {
        imageView.setVisibility(View.GONE);
        return true;
    }

    @Override public boolean onMenuItemActionCollapse(MenuItem item) {
        if (contactList.size() == 0) {
            imageView.postDelayed(new Runnable() {
                @Override public void run() {
                    imageView.setVisibility(View.VISIBLE);
                }
            }, 300);
        }
        return true;
    } // end-interface: MenuItemCompat.OnActionExpandListener


    // start-interface: HxSearchContactView
    @Override public void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override public void stopLoading() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override public void showContacts(List<String> contacts) {
        contactList.clear();
        for (String contact : contacts) {
            contactList.add(new EaseUser(contact));
        }
        easeContactList.refresh();
    }

    @Override public void showSearchError(String error) {
        String info = getString(R.string.hx_contact_error_search, error);
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
    }

    @Override public void showSendInviteResult(boolean success) {
        if (success) {
            Toast.makeText(this, R.string.hx_contact_send_invite_success, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.hx_contact_send_invite_fail, Toast.LENGTH_SHORT).show();
        }
    }

    @Override public void showAlreadyIsFriend() {
        Toast.makeText(this, R.string.hx_contact_already_is_friend, Toast.LENGTH_SHORT).show();
    } // end-interface: HxSearchContactView

    // 接收查询参数
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            suggestions.saveRecentQuery(query, null);
            presenter.searchContact(query);
        }
    }

    private void initSearchView(final SearchView searchView) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override public boolean onSuggestionClick(int position) {
                // 查询当前点击的搜索建议，并在SearchView上显示
                Cursor cursor = searchView.getSuggestionsAdapter().getCursor();
                cursor.moveToPosition(position);
                String data = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_QUERY));
                searchView.setQuery(data, false);
                return false;
            }
        });
    }


}
