package com.aiminerva.oldpeople.ui.ecg;

import com.aiminerva.oldpeople.base.BaseView;
import com.aiminerva.oldpeople.bean.Poinots;

import java.util.List;

/**
 * Created by Administrator on 2017/7/1.
 */

public interface ECGView extends BaseView {
    void onBlueToothState(boolean enable);
    void onDataCallBack(List<Poinots> data);
}
