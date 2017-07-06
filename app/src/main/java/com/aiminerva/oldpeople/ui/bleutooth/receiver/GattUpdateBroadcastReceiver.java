package com.aiminerva.oldpeople.ui.bleutooth.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aiminerva.oldpeople.bluetooth4.ble.service.BluetoothLeService;
import com.aiminerva.oldpeople.bluetooth4.creative.libdemo.Bluetooth4Manager;
import com.aiminerva.oldpeople.ui.bleutooth.BlueToothView;
import com.orhanobut.logger.Logger;

/**
 * Created by Administrator on 2017/6/19.
 */

public class GattUpdateBroadcastReceiver extends BroadcastReceiver {
    private Bluetooth4Manager b4Manager;
    private BlueToothView view;

    public GattUpdateBroadcastReceiver(BlueToothView view, Bluetooth4Manager b4Manager) {
        this.view = view;
        this.b4Manager = b4Manager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (BluetoothLeService.ACTION_BLUETOOTH_RESULT.equals(action)) {
            switch (BluetoothLeService.MODE) {
                case BluetoothLeService.MODE_XUETANG://血糖
                    float bloodsuger = intent.getFloatExtra("bloodsuger", -1);
                    Logger.v("血糖--" + bloodsuger);
                    this.view.onXuetangCallBack(bloodsuger);
                    if (!this.b4Manager.isUpdate && bloodsuger != -1) {
                        //执行数据上传操作
                        this.b4Manager.isUpdate = true;
                    }
                    break;
                case BluetoothLeService.MODE_XUEYANG://血氧
                    int oxygen_value = intent.getIntExtra("oxygen_value", -1);
                    int pulse_value = intent.getIntExtra("pulse_value", -1);
                    Logger.v("血氧--" + oxygen_value + "\n脉搏--" + pulse_value);
                    this.view.onXueyangCallback(oxygen_value, pulse_value);
                    if (!this.b4Manager.isUpdate) {
                        //执行数据上传操作
                        this.b4Manager.isUpdate = true;
                    }
                    break;
                case BluetoothLeService.MODE_XUEYA://血压
                    int systolicpress = intent.getIntExtra("systolicpress", -1);
                    int diastolicpress = intent.getIntExtra("diastolicpress", -1);
                    int plusstate = intent.getIntExtra("plusstate", -1);
                    Logger.v("收缩压--" + systolicpress + "\n舒张压--" + diastolicpress + "\n心率--" + plusstate);
                    this.view.onXueyaCallback(systolicpress, diastolicpress, plusstate);
                    if (!this.b4Manager.isUpdate) {
                        //执行数据上传操作
                        this.b4Manager.isUpdate = true;
                    }
                    break;

            }
        } else if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
            Logger.i("已连接");
        } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            if (this.b4Manager.mManager != null) {
                this.b4Manager.mManager.closeService(context);
            }
            if (this.b4Manager.mFingerOximeter != null)
                this.b4Manager.mFingerOximeter.Stop();
            this.b4Manager.mFingerOximeter = null;
            if (this.b4Manager.mManager != null) {
                this.b4Manager.mManager.reConnect();
                Logger.i("开始连接");
            }

        }
//        else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
//            // Show all the supported services and characteristics on
//            // theuser interface.
//            // showAllCharacteristic();
//            this.b4Manager.isUpdate = false;
//
//        } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
//            //Toast.makeText(MainActivity.this,intent.getStringExtra(BluetoothLeService.EXTRA_DATA),Toast.LENGTH_SHORT).show();
//
//        } else if (BluetoothLeService.ACTION_SPO2_DATA_AVAILABLE.equals(action)) {
//            //byte[] data =intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
//            //Log.d(TAG, "MainActivity received:"+Arrays.toString(data));
//
//        }
        else if (BluetoothLeService.ACTION_CHARACTER_NOTIFICATION.equals(action)) {
            this.b4Manager.startFingerOximeter();

        }
//        else if (BLEManager.ACTION_FIND_DEVICE.equals(action)) {
////				tv_BlueState.setText("find device, start service");
//
//        } else if (BLEManager.ACTION_SEARCH_TIME_OUT.equals(action)) {
////				tv_BlueState.setText("search time out!");
//
//        } else if (BLEManager.ACTION_START_SCAN.equals(action)) {
////				tv_BlueState.setText("discoverying");
//
//        }
    }
}
