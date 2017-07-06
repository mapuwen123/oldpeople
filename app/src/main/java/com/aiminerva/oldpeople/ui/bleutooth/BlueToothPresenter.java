package com.aiminerva.oldpeople.ui.bleutooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.IntentFilter;

import com.aiminerva.oldpeople.base.BasePresenter;
import com.aiminerva.oldpeople.bluetooth4.ble.service.BLEManager;
import com.aiminerva.oldpeople.bluetooth4.creative.libdemo.Bluetooth4Manager;
import com.aiminerva.oldpeople.deviceservice.HealthServiceManager;
import com.aiminerva.oldpeople.ui.bleutooth.receiver.BlueToothBroadcastReceiver;
import com.aiminerva.oldpeople.ui.bleutooth.receiver.GattUpdateBroadcastReceiver;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/6/12.
 */

public class BlueToothPresenter extends BasePresenter<BlueToothView> {
    private BlueToothModel model;

    private BlueToothBroadcastReceiver blueToothBroadcastReceiver;
    public Bluetooth4Manager b4Manager;

    private GattUpdateBroadcastReceiver gattUpdateBroadcastReceiver;

    public BlueToothPresenter() {
        model = new BlueToothModel();
    }

    public void initBluetoothReceiver(Context context) {
        blueToothBroadcastReceiver = new BlueToothBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(blueToothBroadcastReceiver, filter);
    }

    public void unRegistBluetoothReceiver(Context context) {
        context.unregisterReceiver(blueToothBroadcastReceiver);
    }

    public void initGattUpdateReceiver(Context context) {
        gattUpdateBroadcastReceiver = new GattUpdateBroadcastReceiver(mView, b4Manager);
        context.registerReceiver(gattUpdateBroadcastReceiver, BLEManager.makeGattUpdateIntentFilter());
    }

    public void unRegistGattUpdateReceiver(Context context) {
        context.unregisterReceiver(gattUpdateBroadcastReceiver);
    }

    public void loadHealthDevices() {
        if (!HealthServiceManager.getInstance().isBluetoothValid()) {
            mView.error("本机没有找到蓝牙硬件或驱动！");
        } else {
//            HealthServiceManager.getInstance().enableBluetooth(false);
            // 如果本地蓝牙没有开启，则开启
            if (!HealthServiceManager.getInstance().isBluetoothEnable()) {
                HealthServiceManager.getInstance().uinit();
                HealthServiceManager.getInstance().setupChat(true);
            } else {
                b4Manager.mManager.scanLeDevice(true);
                mView.onBlueToothState(true);
            }
        }
    }

    public void initBluetoothManager(Activity activity) {
        b4Manager = new Bluetooth4Manager(activity);
    }
}
