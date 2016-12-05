package com.lujianzhao.androidbasedemo.http.model;

import com.lujianzhao.base.callback.ExecutorCallBack;
import com.lujianzhao.base.common.logutils.LogUtils;
import com.lujianzhao.base.common.rx.RxUtil;
import com.lujianzhao.base.http.progress.domain.ProgressRequest;
import com.lujianzhao.androidbasedemo.http.contract.DownloadContract;
import com.lujianzhao.androidbasedemo.http.model.repositorys.http.GetAndPostClient;
import com.lujianzhao.androidbasedemo.http.model.repositorys.http.GetAndPostService;

import rx.Subscriber;

/**
 * 作者: lujianzhao
 * 创建时间: 2016/06/18 14:51
 * 描述:
 */
public class DownloadModel extends DownloadContract.Model {

    @Override
    public void fileDownload(String path, final ExecutorCallBack<ProgressRequest> requestDataCallBack) {
        GetAndPostService mGetAndPostService = GetAndPostClient.getInstance("http://server.jeasonlzy.com/OkHttpUtils/").createService(GetAndPostService.class);
        getRxManager().add(RxUtil.getDownloadObservable(mGetAndPostService.downloadFile(), path).compose(RxUtil.<ProgressRequest>applySchedulersProgress()).subscribe(new Subscriber<ProgressRequest>() {
            @Override
            public void onStart() {
                requestDataCallBack.onStart();
            }

            @Override
            public void onCompleted() {
                LogUtils.d("下载完成 onCompleted");
                requestDataCallBack.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.d("下载失败 : " + e.getMessage());
                requestDataCallBack.onError(e);
            }

            @Override
            public void onNext(ProgressRequest file) {
                LogUtils.d("下载进度: " + file.getCurrentBytes() + " / " + file.getContentLength());
                requestDataCallBack.onNext(file);
            }
        }));
    }
}