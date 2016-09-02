package com.feicuiedu.readgroup.presentation.books.bookinfo;


import android.support.annotation.NonNull;

import com.feicuiedu.apphx.Preconditions;
import com.feicuiedu.apphx.basemvp.MvpPresenter;
import com.feicuiedu.apphx.model.HxContactManager;
import com.feicuiedu.apphx.model.HxUserManager;
import com.feicuiedu.apphx.model.event.HxErrorEvent;
import com.feicuiedu.apphx.model.event.HxEventType;
import com.feicuiedu.apphx.model.event.HxSimpleEvent;
import com.feicuiedu.readgroup.network.BombClient;
import com.feicuiedu.readgroup.network.entity.BookEntity;
import com.feicuiedu.readgroup.network.entity.UserEntity;
import com.feicuiedu.readgroup.network.event.ChangeLikeEvent;
import com.feicuiedu.readgroup.network.event.GetBookInfoEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class BookInfoPresenter extends MvpPresenter<BookInfoView> {


    @NonNull @Override protected BookInfoView getNullObject() {
        return BookInfoView.NULL;
    }

    public void getBookInfo(String bookId, boolean triggerByUser) {
        if (!triggerByUser) {
            getView().setRefreshing(true);
        }

        BombClient.getInstance()
                .asyncGetBookInfo(bookId);
    }

    public void changeLike(BookEntity bookEntity, boolean isLike){

        getView().setRefreshing(true);
        String userId = HxUserManager.getInstance().getCurrentUserId();
        BombClient.getInstance()
                .asyncChangeLike(isLike, bookEntity, userId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ChangeLikeEvent event){
        if (event.success) {

            Preconditions.checkNotNull(event.bookEntity);

            getView().showLikeActionSuccess(event.isLike);
            BombClient.getInstance()
                    .asyncGetBookInfo(event.bookEntity.getObjectId());
        } else {
            getView().setRefreshing(false);
            getView().showLikeActionFail(event.isLike, event.errorMessage);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetBookInfoEvent event){

        getView().setRefreshing(false);

        String userId = HxUserManager.getInstance().getCurrentUserId();

        if (event.success) {
            getView().showBookInfo(event.book, event.likes);


            if (event.likes == null) {
                getView().toggleLike(false);
                return;
            }

            boolean hasUser = false;

            for (UserEntity userEntity : event.likes) {
                if (userId.equals(userEntity.getObjectId())) {
                    hasUser = true;
                    break;
                }
            }

            getView().toggleLike(!hasUser);
        } else {
            getView().showGetBookInfoFail(event.errorMessage);
        }
    }

    public void sendInvite(String toHxId) {

        HxContactManager.getInstance()
                .asyncSendInvite(toHxId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HxSimpleEvent e) {
        if (e.type != HxEventType.SEND_INVITE) return;
        getView().showSendInviteResult(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HxErrorEvent e) {
        if (e.type != HxEventType.SEND_INVITE) return;
        getView().showSendInviteResult(false);
    }
}
