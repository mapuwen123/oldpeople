package com.aiminerva.oldpeople.ui.heat.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aiminerva.oldpeople.deviceservice.HealthServiceManager;
import com.aiminerva.oldpeople.ui.bleutooth.BlueToothPresenter;
import com.aiminerva.oldpeople.ui.heat.HeatPresenter;
import com.orhanobut.logger.Logger;

/**
 * Created by Administrator on 2017/6/19.
 */

public class BlueToothBroadcastReceiver extends BroadcastReceiver {
    private String action = null;

    private HeatPresenter presenter;

    public BlueToothBroadcastReceiver(HeatPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        action = intent.getAction();
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            int bluetoothState = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", -1) % 10;
            if (bluetoothState == -1 || bluetoothState == 1 || bluetoothState == 3) return;
            if (!HealthServiceManager.getInstance().isBluetoothEnable()) {
                Logger.i("关闭蓝牙");
                this.presenter.mView.onBlueToothState(false);
                HealthServiceManager.getInstance().uinit();
            } else {
                Logger.i("打开蓝牙");
                this.presenter.mView.onBlueToothState(true);
                HealthServiceManager.getInstance().uinit();
                HealthServiceManager.getInstance().setupChat(true);
            }
        }
    }
}
