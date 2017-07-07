package com.aiminerva.oldpeople.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.aiminerva.oldpeople.base.BaseActivity;
import com.aiminerva.oldpeople.common.BuildConfig;
import com.aiminerva.oldpeople.utils.ToastUtil;
import com.orhanobut.logger.Logger;

/**
 * Created by Administrator on 2017/5/25.
 */

public class NetBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkInfo.State wifiState = null;
        NetworkInfo.State mobileState = null;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if (wifiState != null && mobileState != null
                && NetworkInfo.State.CONNECTED != wifiState
                && NetworkInfo.State.CONNECTED == mobileState) {
            // 手机网络连接成功
            Logger.i("已连接至移动网络");
            BuildConfig.INTERNER = true;
        } else if (wifiState != null && mobileState != null
                && NetworkInfo.State.CONNECTED != wifiState
                && NetworkInfo.State.CONNECTED != mobileState) {
            // 手机没有任何的网络
            Logger.e("已断开网络连接");
            BuildConfig.INTERNER = false;
            BaseActivity.showNetSnackbar();
        } else if (wifiState != null && NetworkInfo.State.CONNECTED == wifiState) {
            // 无线网络连接成功
            Logger.i("已连接至WIFI");
            BuildConfig.INTERNER = true;
        }
    }
}
