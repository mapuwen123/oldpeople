package com.aiminerva.oldpeople.ui.login.bean;

import io.realm.RealmObject;

/**
 * Created by Administrator on 2017/6/30.
 */

public class UserBean extends RealmObject {
    private String username;
    private String password;
    private boolean issave;

    @Override
    public String toString() {
        return "UserBean{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", issave=" + issave +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getIssave() {
        return issave;
    }

    public void setIssave(boolean issave) {
        this.issave = issave;
    }
}
