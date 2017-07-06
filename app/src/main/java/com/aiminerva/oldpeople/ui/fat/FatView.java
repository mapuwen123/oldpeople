package com.aiminerva.oldpeople.ui.fat;

import com.aiminerva.oldpeople.base.BaseView;

/**
 * Created by Administrator on 2017/7/1.
 */

public interface FatView extends BaseView {
    int getHeight();
    int getWeight();
    int getMoisture();
    int getFat();
    int getBim();
    void success(String msg);
}
