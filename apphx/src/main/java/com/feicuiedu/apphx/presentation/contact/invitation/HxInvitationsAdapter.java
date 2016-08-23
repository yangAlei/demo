package com.feicuiedu.apphx.presentation.contact.invitation;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.feicuiedu.apphx.R;
import com.feicuiedu.apphx.model.entity.InviteMessage;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.util.ArrayList;
import java.util.List;

class HxInvitationsAdapter extends BaseAdapter {

    private final List<InviteMessage> messages;
    private final OnHandleInvitationListener listener;

    public HxInvitationsAdapter(OnHandleInvitationListener listener) {
        this.messages = new ArrayList<>();
        this.listener = listener;
    }

    @Override public int getCount() {
        return messages.size();
    }

    @Override public Object getItem(int position) {
        return messages.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_invitation, parent, false);
            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        InviteMessage inviteMessage = messages.get(position);
        viewHolder.bind(inviteMessage);

        return convertView;
    }

    public void setData(List<InviteMessage> data) {
        messages.clear();
        messages.addAll(data);
        notifyDataSetChanged();
    }

    public interface OnHandleInvitationListener {
        void onAccept(InviteMessage inviteMessage);

        void onRefuse(InviteMessage inviteMessage);
    }

    private class ViewHolder implements View.OnClickListener {

        final ImageView ivAvatar;
        final Button btnAgree;
        final Button btnRefuse;
        final TextView tvUsername;
        final TextView tvStatus;
        final TextView tvInfo;

        private InviteMessage inviteMessage;

        ViewHolder(View itemView) {
            ivAvatar = (ImageView) itemView.findViewById(R.id.image_avatar);
            btnAgree = (Button) itemView.findViewById(R.id.button_agree);
            btnRefuse = (Button) itemView.findViewById(R.id.button_refuse);
            tvUsername = (TextView) itemView.findViewById(R.id.text_username);
            tvStatus = (TextView) itemView.findViewById(R.id.text_status);
            tvInfo = (TextView) itemView.findViewById(R.id.text_info);

            btnAgree.setOnClickListener(this);
            btnRefuse.setOnClickListener(this);
        }

        public void bind(InviteMessage inviteMessage) {
            this.inviteMessage = inviteMessage;
            updateView();
        }

        @Override public void onClick(View v) {
            if (inviteMessage == null) {
                throw new NullPointerException();
            }

            if (v.getId() == R.id.button_agree) {
                listener.onAccept(inviteMessage);
            } else if (v.getId() == R.id.button_refuse) {
                listener.onRefuse(inviteMessage);
            }
        }

        private void updateView() {
            switch (inviteMessage.getStatus()) {
                case RAW:
                    btnAgree.setVisibility(View.VISIBLE);
                    btnRefuse.setVisibility(View.VISIBLE);
                    tvStatus.setVisibility(View.GONE);
                    tvInfo.setText(R.string.hx_contact_invitation_reason);
                    break;
                case ACCEPTED:
                    btnAgree.setVisibility(View.GONE);
                    btnRefuse.setVisibility(View.GONE);
                    tvStatus.setVisibility(View.VISIBLE);
                    tvStatus.setText(R.string.hx_contact_invitation_agreed);
                    tvInfo.setText(R.string.hx_contact_invitation_reason);
                    break;
                case REFUSED:
                    btnAgree.setVisibility(View.GONE);
                    btnRefuse.setVisibility(View.GONE);
                    tvStatus.setVisibility(View.VISIBLE);
                    tvStatus.setText(R.string.hx_contact_invitation_refused);
                    tvInfo.setText(R.string.hx_contact_invitation_reason);
                    break;
                case REMOTE_ACCEPTED:
                    btnAgree.setVisibility(View.GONE);
                    btnRefuse.setVisibility(View.GONE);
                    tvStatus.setVisibility(View.GONE);
                    tvInfo.setText(R.string.hx_contact_invitation_accept);
                    break;
                default:
                    throw new RuntimeException("Wrong branch!");
            }

            EaseUserUtils.setUserNick(inviteMessage.getFromHxId(), tvUsername);
            EaseUserUtils.setUserAvatar(ivAvatar.getContext(), inviteMessage.getFromHxId(), ivAvatar);
        }
    }
}
