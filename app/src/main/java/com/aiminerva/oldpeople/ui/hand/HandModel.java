package com.aiminerva.oldpeople.ui.hand;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by Administrator on 2017/7/4.
 */

public class HandModel {
    public Observable doXueyaSave(String time, String shousuo, String shuzhang, String maibo) {
        return Observable.create((ObservableOnSubscribe<String>) e -> {
            e.onNext("保存成功");
            e.onComplete();
        });
    }

    public Observable doXueyangSave(String xueyang, String maibo) {
        return Observable.create((ObservableOnSubscribe<String>) e -> {
            e.onNext("保存成功");
            e.onComplete();
        });
    }

    public Observable doXuetangSave(String time, String time_state, String xuetang) {
        return Observable.create((ObservableOnSubscribe<String>) e -> {
            e.onNext("保存成功");
            e.onComplete();
        });
    }
}
