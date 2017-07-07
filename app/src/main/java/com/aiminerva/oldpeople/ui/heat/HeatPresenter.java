package com.aiminerva.oldpeople.ui.heat;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.IntentFilter;

import com.aiminerva.oldpeople.MyApplication;
import com.aiminerva.oldpeople.R;
import com.aiminerva.oldpeople.base.BasePresenter;
import com.aiminerva.oldpeople.deviceservice.HealthServiceManager;
import com.aiminerva.oldpeople.ui.heat.receiver.BlueToothBroadcastReceiver;
import com.orhanobut.logger.Logger;
import com.raiing.adv.RVMBLEAdvInfo;
import com.raiing.bluetooth.RVMBLEManager;
import com.raiing.bluetooth.RVMBLEPeripheral;
import com.raiing.callback.RVMBLEDataService;
import com.raiing.callback.RVMBLEManagerService;
import com.raiing.data.RealBattery;
import com.raiing.data.RealTemperature;

import java.util.UUID;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2017/6/27.
 */

public class HeatPresenter extends BasePresenter<HeatView> implements RVMBLEDataService,
        RVMBLEManagerService {

    /**
     * mDiscovery: Bluetooth discovery and connection operations class *
     */
    private RVMBLEManager mBLEManager;
    /**
     * mPeripheral: Bluetooth devices operate *
     */
    private RVMBLEPeripheral mPeripheral;
    /**
     * isConnected: Is there a connection device *
     */
    private boolean isConnected = false;
    /**
     * 默认用户的UUID
     */
    private static final String DEFAULT_USER_UUID = "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF";

    private String MAC = "";

    private Context context;

    private BlueToothBroadcastReceiver blueToothBroadcastReceiver;

    private HeatModel model;

    public HeatPresenter() {
        model = new HeatModel();
    }

    public void initBluetoothReceiver() {
        blueToothBroadcastReceiver = new BlueToothBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(blueToothBroadcastReceiver, filter);
    }

    public void unRegistBluetoothReceiver() {
        context.unregisterReceiver(blueToothBroadcastReceiver);
    }

    public void loadHealthDevices() {
        if (!HealthServiceManager.getInstance().isBluetoothValid()) {
            mView.error("本机没有找到蓝牙硬件或驱动！");
        } else {
//            HealthServiceManager.getInstance().enableBluetooth(false);
            // 如果本地蓝牙没有开启，则开启
            if (!HealthServiceManager.getInstance().isBluetoothEnable()) {
                HealthServiceManager.getInstance().enableBluetooth(true);
            } else {
                mView.onBlueToothState(true);
            }
        }
    }

    public void BLEManagerInit(Context context) {
        mView.onLinkState("设备正在初始化,请稍后...");
        this.context = context;
        mBLEManager = new RVMBLEManager(this.context, this);
        // 使能自动重连
        mBLEManager.enableAutoConnect();
        // 使能都能进行连接
        mBLEManager.setConnectDeviceType(RVMBLEManager.DEVICE_TYPE_FOR_CHILD);
        // 输出lib中的所有的log
        mBLEManager.setLogInterface(log -> Logger.e("===device===" + log));
    }

    public void startScan() {
        Logger.i("开始扫描");
        mView.onLinkState("初始化已完成,正在寻找可用设备");
        mBLEManager.startScan();
    }

    public void stopScan() {
        Logger.i("停止扫描");
        mBLEManager.stopScan();
    }

//    public void saveHeatsToRealm(List<RealTemperature> datas) {
//        model.saveHeatToRealm(datas);
//    }
//
//    public List<RealTemperature> getHeatsFromRealm() {
//        return model.getHeatFromRealm();
//    }

    public void closeRealm() {
        model.closeRealm();
    }

    //-------------RVMBLEDataService--------------
    @Override
    public void onRealtimeTemperature(int i, int i1, int i2) {
        // 实时温度
        Logger.i("------------onRealtimeTemperature");
        EventBus.getDefault().post("已读取到温度数据");
        RealTemperature realTemperature = new RealTemperature(i, i1);
        EventBus.getDefault().post(realTemperature);
    }

    @Override
    public void onRealtimeTemperature1(int i, int i1, int i2, int i3) {
        // 不需要处理此接口
    }

    @Override
    public void onRaiingInfo(int i, byte[] bytes) {
        // 不需要处理此接口
    }

    @Override
    public void onDeviceManufacturerName(String s) {
//        Logger.d("生产设备厂商名称:" + s);
    }

    @Override
    public void onDeviceHardwareRev(String s) {
//        Logger.d("设备硬件版本:" + s);
    }

    @Override
    public void onDeviceModelNum(String s) {
//        Logger.d("设备型号:" + s);
    }

    @Override
    public void onDeviceSerialNumber(String s) {
//        Logger.d("设备序列号:" + s);
    }

    @Override
    public void onDeviceFirmwareRev(String s) {
//        Logger.d("设备固件版本号:" + s);
    }

    /**
     * 获取实时上传的存储数据大小
     *
     * @param i
     */
    @Override
    public void onStorageUploadDataSize(int i) {

    }

    /**
     * 获取实时上传的存储数据
     *
     * @param s(Json格式)
     */
    @Override
    public void onStorageUploadData(String s) {

    }

    @Override
    public void onStorageUploadProgress(int i, int i1) {
        Logger.e("-------------------onStorageUploadProgress");
        EventBus.getDefault().post("读取进度:" + i1 + "%");
    }

    @Override
    public void onStorageUploadCompleting() {
        // 检查固件升级，如果不需要，直接删除存储数据
        Logger.e("****************onStorageUploadCompleting*********************");
        mPeripheral.deleteStorageData();
    }

    @Override
    public void onStorageUploadCompleted() {
        Logger.d("数据上传完成");
    }

    @Override
    public void onRetrieveUserUUID(String s) {
        Logger.d("User UUID: " + s);
        // 模拟写入用户的UUID,如果用户的UUID为全FF，下位机认为没有用户UUID，会不执行后续的温度请求获取逻辑
        if (s.equals(DEFAULT_USER_UUID)) {
            String uuid = UUID.randomUUID().toString();
            // 必须写入用户的UUID
            mPeripheral.writeUserUUID(uuid);
        }
        // 得到UUID之后到获取到温度之前，可能需要等待较长的时间
        Logger.d("正在获取温度...");
        EventBus.getDefault().post("开始读取温度数据...");
    }

    @Override
    public void onBatteryVolume(int i, int i1) {
//        Logger.d("电量：" + i1);
        // 实时电量
        RealBattery realBattery = new RealBattery(i1);
        EventBus.getDefault().post(realBattery);

        if (i == RVMBLEDataService.CHECK_STORAGE_DATA_UPLOAD) {
            // 开始存储数据上传
            mPeripheral.startUploadStorageData();
        }
    }

    //-------------RVMBLEManagerService--------------

    /**
     * Request to establish a connection
     *
     * @param mac mac Number
     */
    public void requesToConnect(String mac) {
        MAC = mac;
        mBLEManager.connectDeviceWithMac(MAC);
    }

    /**
     * Disconnect Device
     */
    public void disconnect() {
        if (!MAC.equalsIgnoreCase("")) {
            mBLEManager.disconnectDeviceWithMac(MAC);
        }
    }

    @Override
    public void onScan(RVMBLEAdvInfo rvmbleAdvInfo) {
        Logger.i("扫描完成:" + rvmbleAdvInfo.getMacNum());
        EventBus.getDefault().post("找到设备,正在尝试连接...");
        requesToConnect(rvmbleAdvInfo.getMacNum());
    }

    @Override
    public void onConnect(String s, RVMBLEPeripheral rvmblePeripheral) {
        Logger.i("连接完成:" + s);
        EventBus.getDefault().post("设备已连接");
        isConnected = true;
        // After the connection is established, stop scanning
        mBLEManager.stopScan();
        mPeripheral = rvmblePeripheral;
        // Set the receiving interface data
        rvmblePeripheral.setDataService(this);
        // Start discovery broadcast
        mPeripheral.startDiscovery();
    }

    @Override
    public void onDisConnect(String s) {
        EventBus.getDefault().post(MyApplication.getContext().getResources().getString(R.string.device_disconnetion));
    }

    @Override
    public void onConnectingError(String s, int i) {

    }

    @Override
    public void onBluetoothState(int i) {

    }
}
