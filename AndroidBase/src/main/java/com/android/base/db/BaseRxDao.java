package com.android.base.db;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.android.base.common.logutils.LogUtils;
import com.android.base.common.rx.RxUtil;
import com.android.base.db.ormlite.DatabaseUtil;
import com.android.base.db.ormlite.DbCache;
import com.android.base.db.ormlite.DbCallBack;
import com.android.base.db.ormlite.OrmLiteDao;

import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by lujianzhao on 2016/2/29.
 * <p/>
 * 提供同步与异步两种方式读写数据库
 * <p/>
 * 如果使用异步方式读写数据库，必须调用{@link BaseRxDao#subscribe()}方法订阅，
 * 调用{@link BaseRxDao#unsubscribe()}方法取消订阅
 */
public abstract class BaseRxDao<T> extends OrmLiteDao<T> {

    private CompositeSubscription subscriptions;
    private boolean cache;
    private Class<T> clazz;
    private String tableName;

    public BaseRxDao(Context context, Class<T> cls) {
        this(context, cls, true);
    }

    /**
     * @param context context
     * @param cls     表结构clazz
     * @param cache   是否缓存，如果设置缓存，数据查询将优先读取缓存
     */
    public BaseRxDao(Context context, Class<T> cls, boolean cache) {
        super(context, cls);
        this.clazz = cls;
        this.cache = cache;
        tableName = DatabaseUtil.extractTableName(cls);
    }

    /**
     * 订阅读写操作的返回结果
     * <p/>
     * 注意：调用{@link BaseRxDao#unsubscribe()}方法后，如果需要重新订阅读写操作，必须调用此方法
     */
    public void subscribe() {
        subscriptions = RxUtil.getNewCompositeSubIfUnsubscribed(subscriptions);
    }

    /**
     * 异步读写后，必须调用此方法取消订阅
     */
    public void unsubscribe() {
        RxUtil.unsubscribeIfNotNull(subscriptions);
    }

    /**
     * 增加一条记录
     */
    public boolean insert(T t) {
        boolean result = super.insert(t);
        if (result) {
            DbCache.getInstance().clearByTable(tableName);
        }
        return result;
    }

    /**
     * 增加一条记录
     */
    public Observable insertSync(final T t, final DbCallBack<Boolean> listener) {
        return subscribe(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return insert(t);
            }
        }, new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                listener.onComplete(result);
            }
        });
    }

    /**
     * 批量插入;
     */
    public boolean insertForBatch(List<T> list) {
        boolean result = super.insertForBatch(list);
        if (result) {
            DbCache.getInstance().clearByTable(tableName);
        }
        return result;
    }

    /**
     * 批量插入
     */
    public Observable insertForBatchSync(final List<T> list, final DbCallBack<Boolean> listener) {
        return subscribe(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return insertForBatch(list);
            }
        }, new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                listener.onComplete(result);
            }
        });

    }

    /**
     * 清空数据
     */
    public boolean clearTableData() {
        boolean result = super.clearTableData();
        if (result) {
            DbCache.getInstance().clearByTable(tableName);
        }
        return result;
    }

    /**
     * 清空数据
     */
    public Observable clearTableDataSync(final DbCallBack<Boolean> listener) {
        return subscribe(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return clearTableData();
            }
        }, new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                listener.onComplete(result);
            }
        });
    }

    /**
     * 根据id删除记录
     */
    public boolean deleteById(Integer id) {
        boolean result = super.deleteById(id);
        if (result) {
            DbCache.getInstance().clearByTable(tableName);
        }
        return result;
    }

    /**
     * 根据id删除记录
     */
    public Observable deleteByIdSync(final Integer id, final DbCallBack<Boolean> listener) {
        return subscribe(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return deleteById(id);
            }
        }, new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                listener.onComplete(result);
            }
        });
    }

    public List<T> queryForAll() {
        if (!cache) {
            return super.queryForAll();
        }
        String json = DbCache.getInstance().getCache(tableName, "queryForAll");
        List<T> result = JSON.parseArray(json, clazz);
        if (result != null) {
            LogUtils.d("---------query from cache--");
            return result;
        }
        result = super.queryForAll();
        DbCache.getInstance().addCache(tableName, "queryForAll", result);
        return result;
    }

    public Observable queryForAllObservable() {
        return getDbObservable(new Callable() {
            @Override
            public Object call() throws Exception {
                return queryForAll();
            }
        });
    }

    public Observable queryForAllSync(final DbCallBack<List<T>> listener) {
        return subscribe(new Callable<List<T>>() {
            @Override
            public List<T> call() {
                return queryForAll();
            }
        }, new Action1<List<T>>() {
            @Override
            public void call(List<T> result) {
                listener.onComplete(result);
            }
        });
    }

    public <T> Observable<T> subscribe(Callable<T> callable, Action1<T> action) {
        Observable<T> observable = getDbObservable(callable);
        Subscription subscription = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action);
        if (subscriptions == null) {
            throw new RuntimeException("Do you call subscribe()");
        }
        subscriptions.add(subscription);
        return observable;
    }


    private <T> Observable<T> getDbObservable(final Callable<T> func) {
        return Observable.create(
                new Observable.OnSubscribe<T>() {
                    @Override
                    public void call(Subscriber<? super T> subscriber) {
                        try {
                            subscriber.onNext(func.call());
                        } catch (Exception ex) {
                            LogUtils.e("Error reading from the database", ex);
                        }
                    }
                });
    }
}
