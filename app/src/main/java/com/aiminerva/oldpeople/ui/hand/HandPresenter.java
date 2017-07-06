package com.aiminerva.oldpeople.ui.hand;

import com.aiminerva.oldpeople.base.BasePresenter;
import com.aiminerva.oldpeople.base.OnPresenterListener;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/7/4.
 */

public class HandPresenter extends BasePresenter<HandView> implements Observer<String> {
    private HandModel model;

    public HandPresenter() {
        model = new HandModel();
    }

    public void doXueyaSave() {
        mView.showProgress();
        if (mView.getShousuo().length() == 0 || mView.getShousuo() == null) {
            onError(new Throwable("收缩压不能为空"));
            return;
        } else if (mView.getShuzhang().length() == 0 || mView.getShuzhang() == null) {
            onError(new Throwable("舒张压不能为空"));
            return;
        } else if (mView.getMaibo().length() == 0 || mView.getMaibo() == null) {
            onError(new Throwable("脉搏值不能为空"));
            return;
        } else {
            model.doXueyaSave(mView.getTime(), mView.getShousuo(), mView.getShuzhang(), mView.getMaibo())
                    .delaySubscription(2, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this);
        }
    }

    public void doXueyangSave() {
        mView.showProgress();
        if (mView.getXueyang().length() == 0 || mView.getXueyang() == null) {
            onError(new Throwable("血氧值不能为空"));
            return;
        } else if (mView.getMaibo().length() == 0 || mView.getMaibo() == null) {
            onError(new Throwable("脉搏值不能为空"));
            return;
        } else {
            model.doXueyangSave(mView.getXueyang(), mView.getMaibo())
                    .delaySubscription(2, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this);
        }
    }

    public void doXuetangSave() {
        mView.showProgress();
        if (mView.getXuetang().length() == 0 || mView.getXuetang() == null) {
            onError(new Throwable("血糖值不能为空"));
            return;
        } else {
            model.doXuetangSave(mView.getTime(), mView.getTimeState(), mView.getXuetang())
                    .delaySubscription(2, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this);
        }
    }

    // 解除订阅
    private Disposable disposable;

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        disposable = d;
    }

    @Override
    public void onNext(@NonNull String s) {
        mView.hideProgress();
        mView.success(s);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        mView.hideProgress();
        mView.error(e.getMessage());
        disposable.dispose();
    }

    @Override
    public void onComplete() {
        disposable.dispose();
    }
}
