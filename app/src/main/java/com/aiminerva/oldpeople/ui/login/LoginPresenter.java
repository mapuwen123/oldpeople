package com.aiminerva.oldpeople.ui.login;

import com.aiminerva.oldpeople.base.BasePresenter;
import com.aiminerva.oldpeople.ui.login.bean.UserBean;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmResults;

import static com.aiminerva.oldpeople.common.StaticConfig.password;
import static com.aiminerva.oldpeople.common.StaticConfig.username;

/**
 * Created by Administrator on 2017/6/12.
 */

public class LoginPresenter extends BasePresenter<LoginView> implements Observer<String> {
    private LoginModel model;

    public LoginPresenter() {
        model = new LoginModel();
    }

    public void doLogin() {
        mView.showProgress();
        if (mView.getUserName().length() == 0 || mView.getUserName() == null) {
            mView.error("用户名不能为空!");
            mView.hideProgress();
        } else {
            if (mView.getPassword().length() == 0 || mView.getUserName() == null) {
                mView.error("密码不能为空!");
                mView.hideProgress();
            } else {
                model.doLogin(mView.getUserName(), mView.getPassword(), mView.getCheckedState())
//                        .delay(2, TimeUnit.SECONDS)//模拟网络延迟两秒发送
                        .delaySubscription(2, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this);
            }
        }
    }

    public void getUserFromRealm() {
        RealmResults<UserBean> user =  model.getUserFromRealm();
        if (user.size() != 0) {
            username = user.get(0).getUsername();
            password = user.get(0).getPassword();
            mView.setUserName(user.get(0).getUsername());
            mView.setPassword(user.get(0).getPassword());
            mView.setCheckedState(user.get(0).getIssave());
        }
    }

    public void closeRealm() {
        model.closeRealm();
    }

    // 解除订阅
    private Disposable disposable;

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        disposable = d;
    }

    @Override
    public void onNext(@NonNull String s) {
        model.saveUserToRealm(mView.getUserName(), mView.getPassword(), mView.getCheckedState());
        mView.hideProgress();
        mView.success(s);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        mView.hideProgress();
        mView.error(e.getMessage());
        disposable.dispose();
    }

    @Override
    public void onComplete() {
        disposable.dispose();
    }
}
