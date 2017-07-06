package com.aiminerva.oldpeople.ui.login;

import com.aiminerva.oldpeople.base.BaseView;

/**
 * Created by Administrator on 2017/6/12.
 */

public interface LoginView extends BaseView {
    String getUserName();
    void setUserName(String username);
    String getPassword();
    void setPassword(String password);
    boolean getCheckedState();
    void setCheckedState(boolean ischecked);
    void success(String msg);
}
