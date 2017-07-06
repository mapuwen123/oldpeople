package com.aiminerva.oldpeople.base;

/**
 * Created by Administrator on 2017/6/12.
 */

public interface OnPresenterListener<T> {
    void success(T msg);
    void error(String err);
}
