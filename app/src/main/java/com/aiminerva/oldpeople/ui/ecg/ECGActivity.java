package com.aiminerva.oldpeople.ui.ecg;

import android.Manifest;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aiminerva.oldpeople.R;
import com.aiminerva.oldpeople.base.BaseActivity;
import com.aiminerva.oldpeople.bean.DeviceInfo;
import com.aiminerva.oldpeople.bean.FinltopInfo;
import com.aiminerva.oldpeople.bean.Poinots;
import com.aiminerva.oldpeople.bluetooth4.ble.service.BluetoothLeService;
import com.aiminerva.oldpeople.deviceservice.BaseHealthService;
import com.aiminerva.oldpeople.deviceservice.BluetoothChatService;
import com.aiminerva.oldpeople.deviceservice.ETC_HC201Service;
import com.aiminerva.oldpeople.deviceservice.ETC_HC301Service;
import com.aiminerva.oldpeople.deviceservice.ETC_HC503Service;
import com.aiminerva.oldpeople.deviceservice.ETC_HC601Service;
import com.aiminerva.oldpeople.deviceservice.ETC_HC801Service;
import com.aiminerva.oldpeople.deviceservice.FT_BP_88AService;
import com.aiminerva.oldpeople.deviceservice.HealthServiceManager;
import com.aiminerva.oldpeople.deviceservice.Sino_WL1Service;
import com.aiminerva.oldpeople.deviceservice.prt.PrtEtc_HC201B;
import com.aiminerva.oldpeople.globalsettings.GlobalSettings;
import com.aiminerva.oldpeople.ui.newmain.NewMainActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ECGActivity extends BaseActivity<ECGView, ECGPresenter> implements ECGView,
        View.OnClickListener{
    @BindView(R.id.bluetooth)
    ImageView bluetooth;
    @BindView(R.id.progress)
    ImageView progress;
    @BindView(R.id.xinlv_value)
    TextView xinlvValue;
    @BindView(R.id.bluetooth_state)
    TextView bluetoothState;
    @BindView(R.id.line_chart)
    LineChart lineChart;

    private PowerManager powerManager = null;
    private PowerManager.WakeLock wakeLock = null;

    private int mode;
    private int devic_type;

    public String ecgDeviceName;

    //蓝牙开关flag
    private boolean bluetooth_isopen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(NewMainActivity.names[getIntent().getBundleExtra("INTENT").getInt("TITLE_TYPE")]);
        setBack(true);
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(DialogOnDeniedPermissionListener.Builder
                        .withContext(this)
                        .withTitle(R.string.permission_err_title)
                        .withMessage(R.string.permission_err_msg)
                        .withButtonText(R.string.permission_err_btn_confirm)
                        .withIcon(R.mipmap.ic_launcher_round)
                        .build())
                .check();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ecg;
    }

    @Override
    public ECGPresenter initPresenter() {
        return new ECGPresenter();
    }

    @Override
    protected void dataInit() {
        mode = getIntent().getBundleExtra("INTENT").getInt("MEASURE_TYPE");
        devic_type = getIntent().getBundleExtra("INTENT").getInt("DEVIC_TYPE");

        BluetoothLeService.MODE = mode;

        powerManager = (PowerManager) this.getSystemService(this.POWER_SERVICE);
        wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
                "My Lock");

        presenter.initECGB4Manager(this);
        presenter.loadHealthDevices();
        presenter.initBluetoothReceiver(this);
    }

    @Override
    protected void eventInit() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.initLinkStateReceiver(this);
        wakeLock.acquire();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unRegistLinkStateReceiver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (HealthServiceManager.getInstance().isBluetoothEnable()) {
            // 关闭蓝牙
            HealthServiceManager.getInstance().enableBluetooth(false);
            HealthServiceManager manager = HealthServiceManager.getInstance();
            manager.uinit();
        }
        presenter.unRegistBluetoothReceiver(this);
    }

    //-------------View-----------
    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void error(String err) {

    }

    @Override
    public void onBlueToothState(boolean enable) {
        bluetooth_isopen = enable;
        bluetooth.setSelected(enable);
        if (enable) {
            bluetoothState.setText(getResources().getString(R.string.bluetooth_state_lineing));
            showToast("蓝牙已开启");
        } else {
            bluetoothState.setText(getResources().getString(R.string.bluetooth_state_close));
            showToast("蓝牙已关闭");
        }
    }

    @Override
    public void onDataCallBack(List<Poinots> data) {
        bluetoothState.setText(R.string.read_data_success);
    }

    //-------------Click--------------
    @Override
    public void onClick(View view) {
        if (bluetooth_isopen) {
            HealthServiceManager.getInstance().enableBluetooth(false);
        } else {
            HealthServiceManager.getInstance().enableBluetooth(true);
        }
    }
}
