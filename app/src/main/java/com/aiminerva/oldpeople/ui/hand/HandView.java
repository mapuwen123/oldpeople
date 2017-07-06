package com.aiminerva.oldpeople.ui.hand;

import com.aiminerva.oldpeople.base.BaseView;

/**
 * Created by Administrator on 2017/7/4.
 */

public interface HandView extends BaseView {
    void success(String msg);
    String getTime();
    void setTime(String time);
    String getShousuo();
    String getShuzhang();
    String getMaibo();
    String getXueyang();
    String getTimeState();
    void setTimeState(String time_state);
    String getXuetang();
}
