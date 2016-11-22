package com.liangzhicn.androidbasedemo.db.contract;

import com.android.base.db.ormlite.DbCallBack;
import com.android.base.frame.model.BaseModel;
import com.android.base.frame.presenter.BasePresenter;
import com.android.base.frame.view.IBaseView;
import com.liangzhicn.androidbasedemo.db.model.domains.City;

import java.util.List;

/**
 * Created by Administrator on 2016/4/27.
 */
public interface DBContract {

    abstract class Model extends BaseModel {
        /**
         * 插入一条数据
         * @param city 需要插入的数据
         * @param dbCallBack 完成的回调
         */
        public abstract void insertSync(City city, DbCallBack<Boolean> dbCallBack);

        /**
         * 查询所有数据
         * @param dbCallBack
         */
        public abstract void queryForAllSync(DbCallBack<List<City>> dbCallBack);

        /**
         * 删除数据
         * @param dbCallBack
         */
        public abstract void clearTableDataSync(DbCallBack<Boolean> dbCallBack);
    }

    interface View extends IBaseView {

        void clearView();

        /**
         * 刷新界面
         * @param s
         */
        void updateView(String s);
    }

    abstract class Presenter extends BasePresenter<Model,View> {

        public abstract void onBtnClick(android.view.View view);
    }

}
