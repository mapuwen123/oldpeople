package com.aiminerva.oldpeople.base;

/**
 * Created by Administrator on 2017/6/12.
 */

public abstract interface BaseView {
    void showProgress();
    void hideProgress();
    void error(String err);
}
