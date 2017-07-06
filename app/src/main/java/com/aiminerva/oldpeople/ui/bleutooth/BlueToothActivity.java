package com.aiminerva.oldpeople.ui.bleutooth;

import android.Manifest;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.aiminerva.oldpeople.R;
import com.aiminerva.oldpeople.base.BaseActivity;
import com.aiminerva.oldpeople.bluetooth4.ble.service.BluetoothLeService;
import com.aiminerva.oldpeople.deviceservice.HealthServiceManager;
import com.aiminerva.oldpeople.ui.hand.HandActivity;
import com.aiminerva.oldpeople.ui.newmain.NewMainActivity;
import com.aiminerva.oldpeople.utils.PopupUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;

import butterknife.BindView;

public class BlueToothActivity extends BaseActivity<BlueToothView, BlueToothPresenter> implements BlueToothView,
        View.OnClickListener {

    @BindView(R.id.bluetooth)
    ImageView bluetooth;
    @BindView(R.id.entry)
    LinearLayout entry;
    @BindView(R.id.progress)
    ImageView progress;
    @BindView(R.id.bluetooth_state)
    TextView bluetoothState;
    @BindView(R.id.line_chart)
    LineChart lineChart;
    @BindView(R.id.value_group)
    FrameLayout valueGroup;

    private PowerManager powerManager = null;
    private PowerManager.WakeLock wakeLock = null;

    private int mode;
    private int devic_type;

    private boolean bluetooth_isopen = false;

    private PopupWindow popup;

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
        return R.layout.activity_blue_tooth;
    }

    @Override
    public BlueToothPresenter initPresenter() {
        return new BlueToothPresenter();
    }

    @Override
    protected void dataInit() {
        mode = getIntent().getBundleExtra("INTENT").getInt("MEASURE_TYPE");
        devic_type = getIntent().getBundleExtra("INTENT").getInt("DEVIC_TYPE");

        setValueLayout();
        chartInit();
        BluetoothLeService.MODE = mode;

        powerManager = (PowerManager) this.getSystemService(this.POWER_SERVICE);
        wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
                "My Lock");

        presenter.initBluetoothManager(this);
        presenter.loadHealthDevices();
        presenter.initBluetoothReceiver(this);
    }

    private View valueLayout;

    private void setValueLayout() {
        switch (mode) {
            case BluetoothLeService.MODE_XUEYA:
                valueLayout = getLayoutInflater().inflate(R.layout.measure_model_xueya, null);
                break;
            case BluetoothLeService.MODE_XUEYANG:
                valueLayout = getLayoutInflater().inflate(R.layout.measure_model_xueyang, null);
                break;
            case BluetoothLeService.MODE_XUETANG:
                valueLayout = getLayoutInflater().inflate(R.layout.measure_model_xuetang, null);
                setRight(getString(R.string.xietang_time), this);
                break;
        }
        valueGroup.addView(valueLayout);
    }

    private void chartInit() {
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.getDescription().setEnabled(false);
        lineChart.setNoDataText(getString(R.string.chart_no_data));
        lineChart.setNoDataTextTypeface(Typeface.DEFAULT_BOLD);

//        MyMarkerView mv = new MyMarkerView(this, R.layout.chart_marker_view);

//        IMarker im = new MyMarkerView(this, R.layout.chart_marker_view);
//        lineChart.setMarker(im);
//        lineChart.setMarkerView(mv);
        lineChart.setHighlightPerTapEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        YAxis yAxis = lineChart.getAxisLeft();
        lineChart.getAxisRight().setEnabled(false);

        xAxis.setGranularity(1);
//        xAxis.setDrawGridLines(true);
//        xAxis.setAxisMinimum(1);
//        xAxis.setValueFormatter(new MyValueFormatter());

        yAxis.setDrawGridLines(true);
        switch (mode) {
            case BluetoothLeService.MODE_XUEYA:
                yAxis.setAxisMaximum(200.0f);
                yAxis.setAxisMinimum(0.0f);
                break;
            case BluetoothLeService.MODE_XUEYANG:
                yAxis.setAxisMaximum(120.0f);
                yAxis.setAxisMinimum(50.0f);
                break;
            case BluetoothLeService.MODE_XUETANG:
                yAxis.setAxisMaximum(18.0f);
                yAxis.setAxisMinimum(0.0f);
                break;
        }
        yAxis.setLabelCount(8);
        yAxis.enableGridDashedLine(10f, 5f, 2f);
    }

    @Override
    protected void eventInit() {
        bluetooth.setOnClickListener(this);
        entry.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.initGattUpdateReceiver(this);
        wakeLock.acquire();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unRegistGattUpdateReceiver(this);
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
        presenter.b4Manager.mManager.closeService(this);
        presenter.unRegistBluetoothReceiver(this);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void error(String err) {
        showToast(err);
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
    public void onXueyaCallback(int systolicpress, int diastolicpress, int plusstate) {
        bluetoothState.setText(R.string.read_data_success);
//        text.setText("收缩压--" + systolicpress + "\n舒张压--" + diastolicpress + "\n心率--" + plusstate);
//        shousuo.setText(systolicpress);
//        shuzhang.setText(diastolicpress);
//        maibo.setText(plusstate);
    }

    @Override
    public void onXueyangCallback(int oxygen_value, int pulse_value) {
        bluetoothState.setText(R.string.read_data_success);
//        text.setText("血氧--" + oxygen_value + "\n脉搏--" + pulse_value);
    }

    @Override
    public void onXuetangCallBack(float bloodsuger) {
        bluetoothState.setText(R.string.read_data_success);
//        text.setText("血糖--" + bloodsuger);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.right_btn:
                showPopup(view);
                break;
            case R.id.item_1:
                ((TextView) valueLayout.findViewById(R.id.measure_time)).setText("测量时段: " + "空腹");
                popup.dismiss();
                break;
            case R.id.item_2:
                ((TextView) valueLayout.findViewById(R.id.measure_time)).setText("测量时段: " + "饭后2小时");
                popup.dismiss();
                break;
            case R.id.bluetooth:
                if (bluetooth_isopen) {
                    HealthServiceManager.getInstance().enableBluetooth(false);
                } else {
                    HealthServiceManager.getInstance().enableBluetooth(true);
                }
                break;
            case R.id.entry:
                Bundle bundle = new Bundle();
                bundle.putInt("MODE", mode);
                startIntent(HandActivity.class, bundle);
                break;
        }
    }

    private void showPopup(View view) {
        View contentView = getLayoutInflater().inflate(R.layout.xuetang_popup_layout, null);
        LinearLayout item_1 = contentView.findViewById(R.id.item_1);
        LinearLayout item_2 = contentView.findViewById(R.id.item_2);
        item_1.setOnClickListener(this);
        item_2.setOnClickListener(this);
        popup = PopupUtil.Builder()
                .setWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setContentView(contentView)
                .setBackgroundDrawable(new ColorDrawable(0x00000000))
                .setOutsideTouchable(false)
                .setFocusable(true)
                .showAsDropDown(view);
    }
}
