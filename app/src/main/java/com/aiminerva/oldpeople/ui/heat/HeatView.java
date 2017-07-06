package com.aiminerva.oldpeople.ui.heat;

import com.aiminerva.oldpeople.base.BaseView;
import com.raiing.data.RealBattery;
import com.raiing.data.RealTemperature;

/**
 * Created by Administrator on 2017/6/27.
 */

public interface HeatView  extends BaseView{
    void onBlueToothState(boolean enable);
    void onLinkState(String state);
    void onTemperatureCallBack(RealTemperature temperature);
    void onBatteryCallBack(RealBattery battery);
}
