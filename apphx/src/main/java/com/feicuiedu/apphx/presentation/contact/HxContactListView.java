package com.feicuiedu.apphx.presentation.contact;

import com.feicuiedu.apphx.basemvp.MvpView;

import java.util.List;

/**
 * 联系人列表页面的视图接口。
 */
public interface HxContactListView extends MvpView{

    /**
     * @param contacts 环信Id的集合
     */
    void setContacts(List<String> contacts);

    /**
     * 刷新联系人列表
     */
    void refreshContacts();

    /**
     * @param msg 删除用户失败的相关错误信息
     */
    void showDeleteContactFail(String msg);

    HxContactListView NULL = new HxContactListView() {
        @Override public void setContacts(List<String> contacts) {
        }

        @Override public void refreshContacts() {
        }

        @Override public void showDeleteContactFail(String msg) {
        }
    };

}
