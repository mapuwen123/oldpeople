package com.aiminerva.oldpeople.base;

import org.reactivestreams.Subscriber;

/**
 * Created by Administrator on 2017/6/19.
 */

public abstract class BaseCallback<T extends BaseCallModel> implements Subscriber<T> {
    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onNext(T t) {

    }
}
