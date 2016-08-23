package com.feicuiedu.apphx.presentation.contact.invitation;


import com.feicuiedu.apphx.basemvp.MvpView;
import com.feicuiedu.apphx.model.entity.InviteMessage;

import java.util.List;

public interface HxInvitationsView extends MvpView{

    void refreshInvitations(List<InviteMessage> messages);

    void refreshInvitations();

    void showActionFail();


    HxInvitationsView NULL = new HxInvitationsView() {
        @Override public void refreshInvitations(List<InviteMessage> messages) {
        }

        @Override public void refreshInvitations() {
        }

        @Override public void showActionFail() {
        }
    };
}
