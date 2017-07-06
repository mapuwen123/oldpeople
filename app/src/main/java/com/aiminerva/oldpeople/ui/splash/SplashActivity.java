package com.aiminerva.oldpeople.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.aiminerva.oldpeople.R;
import com.aiminerva.oldpeople.common.StaticConfig;
import com.aiminerva.oldpeople.ui.login.LoginActivity;
import com.aiminerva.oldpeople.ui.login.bean.UserBean;
import com.aiminerva.oldpeople.ui.newmain.NewMainActivity;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<UserBean> query = realm.where(UserBean.class);
        RealmResults<UserBean> user = query.findAll();
        Intent intent = new Intent();
        if (user.size() != 0) {
            StaticConfig.username = user.get(0).getUsername();
            StaticConfig.password = user.get(0).getPassword();
            intent.setClass(this, NewMainActivity.class);
        } else {
            intent.setClass(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
