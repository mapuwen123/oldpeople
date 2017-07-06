package com.aiminerva.oldpeople.ui.newmain;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.aiminerva.oldpeople.R;
import com.aiminerva.oldpeople.base.BaseActivity;
import com.aiminerva.oldpeople.bean.DeviceInfo;
import com.aiminerva.oldpeople.bluetooth4.ble.service.BluetoothLeService;
import com.aiminerva.oldpeople.ui.bleutooth.BlueToothActivity;
import com.aiminerva.oldpeople.ui.ecg.ECGActivity;
import com.aiminerva.oldpeople.ui.fat.FatActivity;
import com.aiminerva.oldpeople.ui.heat.HeatActivity;
import com.aiminerva.oldpeople.ui.newmain.adapter.NewMainAdapter;
import com.aiminerva.oldpeople.ui.newmain.bean.MenuBean;
import com.aiminerva.oldpeople.utils.ActivityUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class NewMainActivity extends BaseActivity<NewMainView, NewMainPresenter> implements NewMainView,
        BaseQuickAdapter.OnItemClickListener{
    @BindView(R.id.recycler)
    RecyclerView recycler;

    private int[] imgs = {
            R.drawable.physical_icon_xueya, R.drawable.physical_icon_xindian,
            R.drawable.physical_icon_xueyang, R.drawable.physical_icon_xuetang,
            R.drawable.physical_icon_xuezhi, R.drawable.physical_icon_tiwen
    };

    public static int[] names = {
            R.string.blood_pressure, R.string.electrocardio,
            R.string.blood_oxygen, R.string.blood_glucose,
            R.string.body_fat, R.string.animal_heat
    };

    private List<MenuBean> menuList = new ArrayList<>();

    private NewMainAdapter adapter;

    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.physicals_normal));
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            exitTime = System.currentTimeMillis();
            showToast("再按一次退出");
            return;
        } else {
            ActivityUtils.removeAllActivity();
        }
        super.onBackPressed();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_main;
    }

    @Override
    public NewMainPresenter initPresenter() {
        return new NewMainPresenter();
    }

    @Override
    protected void dataInit() {
        for (int i = 0; i < imgs.length; i ++) {
            MenuBean menu = new MenuBean();
            menu.setName(getResources().getString(names[i]));
            menu.setImg(imgs[i]);
            menuList.add(menu);
        }

        adapter = new NewMainAdapter(R.layout.examination_menu_item, menuList);
        adapter.setOnItemClickListener(this);
        recycler.setLayoutManager(new GridLayoutManager(this, 2));
        recycler.setAdapter(adapter);
    }

    //------------View----------
    @Override
    protected void eventInit() {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void error(String err) {

    }

    //------------onItemClick----------
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        int mode = 7;
        int devic_type = 7;
        Class activity = null;

        Intent intent = new Intent();
        switch (position) {
            case 0://血压
                mode = BluetoothLeService.MODE_XUEYA;
                devic_type = DeviceInfo.DEVICE_TYPE_BLOODPRESSURE;
                activity = BlueToothActivity.class;
                break;
            case 1://心电
                mode = BluetoothLeService.MODE_XINDIAN;
                devic_type = DeviceInfo.DEVICE_TYPE_EGC;
                activity = ECGActivity.class;
                break;
            case 2://血氧
                mode = BluetoothLeService.MODE_XUEYANG;
                devic_type = DeviceInfo.DEVICE_TYPE_BLOODOXYGEN;
                activity = BlueToothActivity.class;
                break;
            case 3://血糖
                mode = BluetoothLeService.MODE_XUETANG;
                devic_type = DeviceInfo.DEVICE_TYPE_BLOODSUGER;
                activity = BlueToothActivity.class;
                break;
            case 4://体脂
                mode = BluetoothLeService.MODE_TIZHI;
                devic_type = DeviceInfo.DEVICE_TYPE_BODYFAT;
                activity = FatActivity.class;
                break;
            case 5://体温
                mode = BluetoothLeService.MODE_TIWEN;
                devic_type = DeviceInfo.DEVICE_TYPE_TEMP;
                activity = HeatActivity.class;
                break;
        }
        if (mode != 7) {
            Bundle bundle = new Bundle();
            bundle.putInt("TITLE_TYPE", position);
            bundle.putInt("MEASURE_TYPE", mode);
            bundle.putInt("DEVIC_TYPE", devic_type);
            startIntent(activity, bundle);
        }
    }
}
