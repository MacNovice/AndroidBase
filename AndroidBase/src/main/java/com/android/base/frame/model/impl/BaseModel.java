package com.android.base.frame.model.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.base.common.rx.RxManager;
import com.android.base.frame.model.IBaseModel;

/**
 * 作者: lujianzhao
 * 创建时间: 2016/06/18 11:34
 * 描述:
 */
public abstract class BaseModel implements IBaseModel {

    protected RxManager mRxManager;

    protected Context mContext;

    @Override
    public void onCreate() {

    }


    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        mContext = null;
        mRxManager = null;
    }

    @Override
    public void setRxManager(@NonNull RxManager rxManager) {
        this.mRxManager = rxManager;
    }

    @Override
    public void setContext(Context context) {
        this.mContext = context;
    }
}
