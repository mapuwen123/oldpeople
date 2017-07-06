package com.aiminerva.oldpeople.ui.fat;

import com.aiminerva.oldpeople.base.BasePresenter;
import com.aiminerva.oldpeople.base.OnPresenterListener;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/7/1.
 */

public class FatPresenter extends BasePresenter<FatView> implements Observer<String> {
    private FatModel model;

    public FatPresenter() {
        model = new FatModel();
    }

    public void submitData() {
        mView.showProgress();
        if (mView.getWeight() == 0) {
            onError(new Throwable("身高不能为空"));
            return;
        } else if (mView.getMoisture() == 0) {
            onError(new Throwable("体重不能为空"));
            return;
        } else if (mView.getMoisture() == 0) {
            onError(new Throwable("水分含量不能为空"));
            return;
        } else if (mView.getFat() == 0) {
            onError(new Throwable("脂肪含量不能为空"));
            return;
        } else if (mView.getBim() == 0) {
            onError(new Throwable("BIM不能为空"));
            return;
        } else {
            model.submotData(mView.getHeight(),
                    mView.getWeight(),
                    mView.getMoisture(),
                    mView.getFat(),
                    mView.getBim())
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
