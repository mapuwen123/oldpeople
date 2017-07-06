package com.aiminerva.oldpeople.ui.fat;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by Administrator on 2017/7/1.
 */

public class FatModel {
    public Observable submotData(int height, int weight, int moisture, int fat, int bim) {
        return Observable.create((ObservableOnSubscribe<String>) e -> {
            e.onNext("上传成功");
            e.onComplete();
        });
    }
}
