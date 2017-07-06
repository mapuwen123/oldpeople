package com.aiminerva.oldpeople.ui.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.aiminerva.oldpeople.R;
import com.aiminerva.oldpeople.base.BaseActivity;
import com.aiminerva.oldpeople.ui.newmain.NewMainActivity;

import butterknife.BindView;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity<LoginView, LoginPresenter> implements LoginView,
        View.OnClickListener {

    @BindView(R.id.user_name)
    EditText userName;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.login_in)
    Button loginIn;
    @BindView(R.id.check_save)
    CheckBox checkSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.login_page_name));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.closeRealm();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public LoginPresenter initPresenter() {
        return new LoginPresenter();
    }

    @Override
    protected void dataInit() {
        presenter.getUserFromRealm();
    }

    @Override
    protected void eventInit() {
        loginIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        loginIn.setEnabled(false);
        presenter.doLogin();
    }

    @Override
    public void showProgress() {
        getProgress().show();
    }

    @Override
    public void hideProgress() {
        getProgress().dismiss();
    }

    @Override
    public String getUserName() {
        return this.userName.getText().toString();
    }

    @Override
    public void setUserName(String username) {
        this.userName.setText(username);
    }

    @Override
    public String getPassword() {
        return this.password.getText().toString();
    }

    @Override
    public void setPassword(String password) {
        this.password.setText(password);
    }

    @Override
    public boolean getCheckedState() {
        return checkSave.isChecked();
    }

    @Override
    public void setCheckedState(boolean ischecked) {
        checkSave.setChecked(ischecked);
    }

    @Override
    public void success(String msg) {
        showToast(msg);
        startIntent(NewMainActivity.class, null);
//        startIntent(MainActivity.class, null);
        finish();
    }

    @Override
    public void error(String err) {
        loginIn.setEnabled(true);
        showToast(err);
    }
}

