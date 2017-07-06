package com.aiminerva.oldpeople.ui.bleutooth;

import com.aiminerva.oldpeople.base.BaseView;

/**
 * Created by Administrator on 2017/6/12.
 */

public interface BlueToothView extends BaseView {
    void onBlueToothState(boolean enable);
    void onXueyaCallback(int systolicpress, int diastolicpress, int plusstate);
    void onXueyangCallback(int oxygen_value, int pulse_value);
    void onXuetangCallBack(float bloodsuger);
}
