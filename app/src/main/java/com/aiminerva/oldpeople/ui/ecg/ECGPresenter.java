package com.aiminerva.oldpeople.ui.ecg;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;

import com.aiminerva.oldpeople.base.BasePresenter;
import com.aiminerva.oldpeople.base.OnPresenterListener;
import com.aiminerva.oldpeople.bean.DeviceInfo;
import com.aiminerva.oldpeople.bean.FinltopInfo;
import com.aiminerva.oldpeople.bean.Poinots;
import com.aiminerva.oldpeople.bluetooth4.creative.libdemo.ECGBluetooth4Manager;
import com.aiminerva.oldpeople.deviceservice.BaseHealthService;
import com.aiminerva.oldpeople.deviceservice.BluetoothChatService;
import com.aiminerva.oldpeople.deviceservice.ETC_HC201Service;
import com.aiminerva.oldpeople.deviceservice.FT_BP_88AService;
import com.aiminerva.oldpeople.deviceservice.HealthServiceManager;
import com.aiminerva.oldpeople.deviceservice.prt.PrtEtc_HC201B;
import com.aiminerva.oldpeople.globalsettings.GlobalSettings;
import com.aiminerva.oldpeople.ui.ecg.receiver.BlueToothBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/7/1.
 */

public class ECGPresenter extends BasePresenter<ECGView> implements ETC_HC201Service.ETC_HC201ServiceListener{

    private BlueToothBroadcastReceiver blueToothBroadcastReceiver;
    public ECGBluetooth4Manager ecgBluetooth4Manager;

    public String ecgDeviceName;

    public void initBluetoothReceiver(Context context) {
        blueToothBroadcastReceiver = new BlueToothBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(blueToothBroadcastReceiver, filter);
    }

    public void unRegistBluetoothReceiver(Context context) {
        context.unregisterReceiver(blueToothBroadcastReceiver);
    }

    public void initLinkStateReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(ecgBluetooth4Manager.receiver, intentFilter);
    }

    public void unRegistLinkStateReceiver(Context context) {
        context.unregisterReceiver(ecgBluetooth4Manager.receiver);
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
                ecgBluetooth4Manager.scanLeDevice(true);
                mView.onBlueToothState(true);
            }
        }
    }

    public void initECGB4Manager(Context context) {
        ecgBluetooth4Manager = new ECGBluetooth4Manager((ECGActivity) context, this);
    }

    // 切换蓝牙配对的方法
    public BaseHealthService service = null;
    public void switchHealthDevice(int deviceType) {

       /* public final static int DEVICE_TYPE_BODYFAT = 6;// ：体质
        public final static int DEVICE_TYPE_BLOODPRESSURE = 1;// ：血压
        public final static int DEVICE_TYPE_BLOODSUGER = 2;// ：血糖
        public final static int DEVICE_TYPE_BLOODOXYGEN = 4;// ：血氧
        public final static int DEVICE_TYPE_EGC = 5;// ：心电
        public final static int DEVICE_TYPE_TEMP = 3;// 温度计*/

        HealthServiceManager.getInstance().setupChat(true);
        service = null;

        HealthServiceManager manager = HealthServiceManager.getInstance();
        ArrayList<BaseHealthService> serviceList = manager.getServiceList();
        String deviceName = "";

        // 获取配对设备的列表
        for (BaseHealthService it : serviceList) {

            if (deviceType == DeviceInfo.DEVICE_TYPE_EGC) {
                // 心电
                deviceName = "心电仪";
                ecgDeviceName = PrtEtc_HC201B.UDID;
                if (it instanceof ETC_HC201Service) {
                    service = it;
                    break;
                } else if (it instanceof FT_BP_88AService) {
                    FinltopInfo finltop = GlobalSettings.getInstance().getFinltopInfo();
                    if (finltop.hasFinltop2Mac(it.getAddress())) {//2 心电
                        service = it;
                        break;
                    } else if (!finltop.hasFinltop2Mac() && !finltop.hasFinltop3Mac(it.getAddress())) {
                        service = it;
                        break;
                    }
                    continue;
                }
            }
        }

        if (service == null) {
            // TODO 没有找到设备

            // 断开其他设备的蓝牙连接
            for (BaseHealthService it : serviceList) {
                HealthServiceManager.getInstance().diconnect(it);
            }

            return;
        } else {
            // 断开其他设备的蓝牙连接
            for (BaseHealthService it : serviceList) {
                if (it != service)
                    HealthServiceManager.getInstance().diconnect(it);
            }

            service.addListener(this);
            if (service.getConnectStatus() == BluetoothChatService.EnumBlutToothState.STATE_NONE) {
                // 如果存在此设备并处于未连接状态，进行配对连接
                HealthServiceManager.getInstance().connect(service);
            }
        }
    }

    //------------心电仪回调监听-------------
    @Override
    public void onUpdateECGFromList(List<Poinots> data) {
        mView.onDataCallBack(data);
    }

    @Override
    public void onUpdateECGFromFile(String path) {

    }

    @Override
    public void onUpdateError(ETC_HC201Service sevice, String sErr) {

    }

    @Override
    public void onConnecting(BaseHealthService service) {

    }

    @Override
    public void onConnected(BaseHealthService service) {

    }

    @Override
    public void onDisConnected(BaseHealthService service) {

    }

    @Override
    public void onConnectFailed(BaseHealthService service) {

    }
}
