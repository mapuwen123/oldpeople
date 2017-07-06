package com.aiminerva.oldpeople.ui.login;

import com.aiminerva.oldpeople.common.StaticConfig;
import com.aiminerva.oldpeople.ui.login.bean.UserBean;

import io.reactivex.Observable;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by Administrator on 2017/6/12.
 */

public class LoginModel {

    private Realm realm;

    public LoginModel() {
        realm = Realm.getDefaultInstance();
    }

    /**
     * 登陆
     * @param username
     * @param password
     * @param save
     * @return
     */
    public Observable doLogin(String username, String password, boolean save) {
        Observable<String> observable = Observable.create(e -> {
            if (username.equalsIgnoreCase("admin")) {
                if (password.equalsIgnoreCase("123")) {
                    StaticConfig.username = username;
                    StaticConfig.password = password;
                    e.onNext("登陆成功");
                    e.onComplete();
                } else {
                    e.onError(new Throwable("密码错误"));
                }
            } else {
                e.onError(new Throwable("用户名错误"));
            }
        });
        return observable;
    }

    /**
     * 保存用户到Realm
     * @param username
     * @param password
     * @param save
     */
    public void saveUserToRealm(String username, String password, boolean save) {
        // 创建Realm事务,保存用户数据
        realm.executeTransactionAsync(realm1 -> {
            RealmResults<UserBean> users = realm1.where(UserBean.class).findAll();
            if (users.size() == 0) {
                UserBean user = realm1.createObject(UserBean.class);
                user.setUsername(username);
                user.setPassword(password);
                user.setIssave(save);
            } else {
                users.get(0).setUsername(username);
                users.get(0).setPassword(password);
                users.get(0).setIssave(save);
            }
        });
    }

    /**
     * 从Realm中读取用户
     * @return
     */
    public RealmResults<UserBean> getUserFromRealm() {
        RealmQuery<UserBean> query = realm.where(UserBean.class);
        RealmResults<UserBean> user = query.findAll();
        return user;
    }

    public void closeRealm() {
        realm.close();
    }
}
