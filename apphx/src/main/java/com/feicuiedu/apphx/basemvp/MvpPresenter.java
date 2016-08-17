package com.feicuiedu.apphx.basemvp;


import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

public abstract class MvpPresenter<V extends MvpView> {

    private V view;

    public final void onCreate() {
        EventBus.getDefault().register(this);
    }

    public final void attachView(V view){
        this.view = view;
    }


    public final void detachView(){
        this.view = getNullObject();
    }

    public final void onDestroy(){
        EventBus.getDefault().unregister(this);
    }

    protected final V getView(){
        return view;
    }

    protected abstract @NonNull V getNullObject();
}
