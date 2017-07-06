package com.aiminerva.oldpeople.base;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aiminerva.oldpeople.MyApplication;
import com.aiminerva.oldpeople.R;
import com.aiminerva.oldpeople.utils.ActivityUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by mapuw on 2016/12/6.
 */

public abstract class BaseActivity<V, P extends BasePresenter<V>> extends AppCompatActivity {

    protected final String TAG = this.getClass().getSimpleName();

    private Toolbar toolbar;
    private TextView title;
    private RelativeLayout back;
    private RelativeLayout rightBtn;
    private TextView rightTxt;

    private static FrameLayout content;

    private Unbinder un;

    public P presenter;

    private AlertDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.addActivity(this);
        setContentView(getLayoutId());
        un = ButterKnife.bind(this);
        presenter = initPresenter();
        presenter.attach((V) this);
        dataInit();
        eventInit();
    }

    protected abstract int getLayoutId();

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        View view = getLayoutInflater().inflate(R.layout.base_layout, null);
        toolbar = view.findViewById(R.id.toolbar);
        title = view.findViewById(R.id.title);
        back = view.findViewById(R.id.back_btn);
        rightBtn = view.findViewById(R.id.right_btn);
        rightTxt = view.findViewById(R.id.right_text);
        back.setVisibility(View.GONE);
        rightBtn.setVisibility(View.GONE);
        super.setContentView(view);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            view.setFitsSystemWindows(true);
        }

        initDefaultView(layoutResID);
        setSupportActionBar(toolbar);
    }

    private void initDefaultView(@LayoutRes int layoutResId) {
        View childView = LayoutInflater.from(this).inflate(layoutResId, null);
        content = (FrameLayout) findViewById(R.id.content);
        content.addView(childView, 0);
    }

    public void setTitle(CharSequence titleChar) {
        title.setText(titleChar);
    }

    public void setBack(boolean enable) {
        if (enable) {
            back.setVisibility(View.VISIBLE);
            back.setOnClickListener(v -> onBackPressed());
        } else {
            back.setVisibility(View.GONE);
        }
    }

    public void setRight(CharSequence rightChar, View.OnClickListener listener) {
        rightBtn.setVisibility(View.VISIBLE);
        rightTxt.setText(rightChar);
        rightBtn.setOnClickListener(listener);
    }

    // 实例化presenter
    public abstract P initPresenter();

    protected abstract void dataInit();

    protected abstract void eventInit();

    @Override
    protected void onDestroy() {
        ActivityUtils.removeActivity(this);
        un.unbind();
        presenter.dettach();
        super.onDestroy();
    }


    /**
     * 页面跳转
     *
     * @param activity（完整类名）
     * @param bundle
     */
    public void startIntent(Class activity, @Nullable Bundle bundle) {
        Intent intent = null;
        try {
            intent = new Intent(this, Class.forName(activity.getCanonicalName()));
            if (bundle != null) {
                intent.putExtra("INTENT", bundle);
            }
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 弹出Toast
     *
     * @param msg
     */
    public void showToast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 弹出Alert
     *
     * @param title
     * @param msg
     * @param positive
     * @param negative
     * @param positivelistener
     * @param negativelistener
     */
    public void showAlertDialog(CharSequence title,
                                CharSequence msg,
                                @Nullable CharSequence positive,
                                @Nullable CharSequence negative,
                                @Nullable DialogInterface.OnClickListener positivelistener,
                                @Nullable DialogInterface.OnClickListener negativelistener) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        if (title != null) {
            alert.setTitle(title);
        }
        alert.setMessage(msg);
        if (positive != null) {
            alert.setPositiveButton(positive, positivelistener);
        }
        if (negative != null) {
            alert.setNegativeButton(negative, negativelistener);
        }
        alert.create().show();
    }

    public AlertDialog getProgress() {
        if (progress == null) {
            progress = new AlertDialog.Builder(this).create();
            progress.setCancelable(false);
            progress.show();
            Window win = progress.getWindow();
            win.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams layoutParams = progress.getWindow().getAttributes();
            layoutParams.width = 300;
            layoutParams.height = 300;
            progress.getWindow().setAttributes(layoutParams);
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_progress, null);
            win.setContentView(view);
        }
        return progress;
    }

    public static void showNetSnackbar() {
        Snackbar.make(content, MyApplication.getContext().getString(R.string.no_net), Snackbar.LENGTH_LONG)
                .setAction(MyApplication.getContext().getString(R.string.open_set), view -> {
                    Intent intent=null;
                    //判断手机系统的版本  即API大于10 就是3.0或以上版本
                    if(android.os.Build.VERSION.SDK_INT>10){
                        intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                    }else{
                        intent = new Intent();
                        ComponentName component = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
                        intent.setComponent(component);
                        intent.setAction("android.intent.action.VIEW");
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MyApplication.getContext().startActivity(intent);
                }).show();
    }

}
