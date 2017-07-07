package com.aiminerva.oldpeople.ui.heat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aiminerva.oldpeople.R;
import com.aiminerva.oldpeople.base.BaseActivity;
import com.aiminerva.oldpeople.deviceservice.HealthServiceManager;
import com.aiminerva.oldpeople.ui.examination.ExaminationFragment;
import com.aiminerva.oldpeople.widget.MyMarkerView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.orhanobut.logger.Logger;
import com.raiing.data.RealBattery;
import com.raiing.data.RealTemperature;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/****
 * 测量体温
 */
public class HeatActivity extends BaseActivity<HeatView, HeatPresenter> implements HeatView,
        View.OnClickListener {

    @BindView(R.id.bluetooth)
    ImageView bluetooth;
    @BindView(R.id.power)
    TextView power;
    @BindView(R.id.progress)
    ImageView progress;
    @BindView(R.id.heat_value)
    TextView heatValue;
    @BindView(R.id.bluetooth_state)
    TextView bluetoothState;
    @BindView(R.id.line_chart)
    LineChart lineChart;

    private boolean bluetooth_isopen = false;

    private PowerManager powerManager = null;
    private PowerManager.WakeLock wakeLock = null;

    private List<Entry> chart_heats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(ExaminationFragment.names[getIntent().getBundleExtra("INTENT").getInt("TITLE_TYPE")]);
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
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.disconnect();
        EventBus.getDefault().unregister(this);
        BluetoothAdapter.getDefaultAdapter().disable();
        presenter.unRegistBluetoothReceiver();
        presenter.closeRealm();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_heat;
    }

    @Override
    public HeatPresenter initPresenter() {
        return new HeatPresenter();
    }

    @Override
    protected void dataInit() {
        powerManager = (PowerManager) this.getSystemService(this.POWER_SERVICE);
        wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
                "My Lock");

        presenter.BLEManagerInit(this);
        presenter.loadHealthDevices();
        presenter.initBluetoothReceiver();

        chartInit();
        chart_heats = new ArrayList<>();
    }

    @Override
    protected void eventInit() {
        bluetooth.setOnClickListener(this);
    }

    private XAxis xAxis;

    private void chartInit() {
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.getDescription().setEnabled(false);
        lineChart.setNoDataText(getString(R.string.chart_no_data));
        lineChart.setNoDataTextTypeface(Typeface.DEFAULT_BOLD);

        MyMarkerView marker = new MyMarkerView(this, R.layout.custom_marker_view);

        lineChart.setMarker(marker);
        lineChart.setHighlightPerTapEnabled(false);

        xAxis = lineChart.getXAxis();
        YAxis yAxis = lineChart.getAxisLeft();
        lineChart.getAxisRight().setEnabled(false);

        xAxis.setGranularity(1);
        xAxis.setDrawGridLines(true);
        xAxis.setAxisMinimum(1);
//        xAxis.setValueFormatter(new MyValueFormatter());

        yAxis.setDrawGridLines(true);
        yAxis.setAxisMaximum(42.0f);
        yAxis.setAxisMinimum(25.0f);
        yAxis.setLabelCount(5);
//        yAxis.removeAllLimitLines(); // reset all limit lines to avoid

        yAxis.setStartAtZero(false);
        yAxis.enableGridDashedLine(10f, 10f, 0f);

        // limit lines are drawn behind data (and not on top)
        yAxis.setDrawLimitLinesBehindData(true);

        Legend l = lineChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
    }

    private void setChartData(List<Entry> data) {
        LineDataSet setHeat = new LineDataSet(data, "体温");
        setHeat.setColor(ContextCompat.getColor(this, R.color.temputure_line));
        setHeat.setCircleColor(ContextCompat.getColor(this, R.color.temputure_line));
        setHeat.setFillColor(getResources().getColor(R.color.temputure_line));
        setHeat.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        setHeat.setAxisDependency(YAxis.AxisDependency.LEFT);
        setHeat.enableDashedLine(10f, 0f, 0f);
        setHeat.setLineWidth(3f);
        setHeat.setCircleSize(5f);
        setHeat.setDrawValues(true);
        setHeat.setValueTextSize(0f);
        setHeat.setDrawFilled(true);

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setHeat);

        LineData dataHeat = new LineData(dataSets);

        xAxis.setLabelCount(chart_heats.size());
        lineChart.setData(dataHeat);
        lineChart.animateX(1000, Easing.EasingOption.EaseInOutQuart);
    }

    /**
     * eventBus事件总线,获取实时的温度
     */
    public void onEventMainThread(RealTemperature temp) {
        long temperature = temp.getTempeature();
        onTemperatureCallBack(temp);
    }

    /**
     * eventBus事件总线,获取实时的电池电量
     */
    public void onEventMainThread(RealBattery temp) {
        long battery = temp.getBattery();
        onBatteryCallBack(temp);
    }

    public void onEventMainThread(String state) {
        onLinkState(state);
    }

    //-------------View--------------
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
            showToast("蓝牙已开启");
            onLinkState("蓝牙已开启");
            presenter.startScan();
        } else {
            showToast("蓝牙已关闭");
            onLinkState("蓝牙已关闭");
        }
    }

    @Override
    public void onLinkState(String state) {
        bluetoothState.setText(state);
    }

    // 解除订阅
    private Disposable disposable;

    @Override
    public void onTemperatureCallBack(RealTemperature temperature) {
        long temp = temperature.getTempeature();
        heatValue.setText(new DecimalFormat("#.00")
                .format(((double) temp / 1000)) + " ℃");
        Logger.d("温度:" + new DecimalFormat("#.00")
                .format(((double) temp / 1000)) + " ℃");
        Observable.create((ObservableOnSubscribe<Entry>) e -> {
            if (chart_heats.size() == 7) {
                chart_heats.remove(0);
            }

            List<Entry> temporaryData = new ArrayList<>();
            for (int i = 0; i < chart_heats.size(); i++) {
                Entry heat = new Entry();
                heat.setX(i);
                heat.setY(chart_heats.get(i).getY());
                temporaryData.add(heat);
            }
            chart_heats.clear();
            chart_heats.addAll(temporaryData);
            Entry heat = new Entry();
            heat.setX(chart_heats.size());
            heat.setY(Float.parseFloat(new DecimalFormat("#.00")
                    .format(((double) temp / 1000))));
            e.onNext(heat);
            e.onComplete();
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Entry>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Entry entry) {
                        chart_heats.add(entry);
                        setChartData(chart_heats);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        disposable.dispose();
                    }

                    @Override
                    public void onComplete() {
                        disposable.dispose();
                    }
                });
    }

    @Override
    public void onBatteryCallBack(RealBattery battery) {
        long batt = battery.getBattery();
        power.setText("电量：" + batt);
        Logger.d("电量：" + batt);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bluetooth:
                if (bluetooth_isopen) {
                    HealthServiceManager.getInstance().enableBluetooth(false);
                } else {
                    HealthServiceManager.getInstance().enableBluetooth(true);
                }
                break;
        }
    }
}
