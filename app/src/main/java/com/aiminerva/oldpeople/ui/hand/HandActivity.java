package com.aiminerva.oldpeople.ui.hand;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.aiminerva.oldpeople.R;
import com.aiminerva.oldpeople.base.BaseActivity;
import com.aiminerva.oldpeople.bluetooth4.ble.service.BluetoothLeService;
import com.aiminerva.oldpeople.ui.hand.inputlayouts.BaseInputView;
import com.aiminerva.oldpeople.ui.hand.inputlayouts.XueTangLayoutInit;
import com.aiminerva.oldpeople.ui.hand.inputlayouts.XueYaLayoutInit;
import com.aiminerva.oldpeople.ui.hand.inputlayouts.XueYangLayoutInit;
import com.aiminerva.oldpeople.utils.PopupUtil;
import com.aiminerva.oldpeople.utils.TimeUtil;

import butterknife.BindView;

public class HandActivity extends BaseActivity<HandView, HandPresenter> implements HandView,
        View.OnClickListener {

    @BindView(R.id.input_layout)
    FrameLayout inputLayout;
    @BindView(R.id.submit)
    Button submit;

    private int mode;
    private BaseInputView inputView_init;

    private PopupWindow time_popup;
    private PopupWindow time_state_popup;

    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.hand_action_title));
        setBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        释放输入子布局
        inputView_init.release();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_hand;
    }

    @Override
    public HandPresenter initPresenter() {
        return new HandPresenter();
    }

    @Override
    protected void dataInit() {
        mode = getIntent().getBundleExtra("INTENT").getInt("MODE");
        if (mode == BluetoothLeService.MODE_XUEYA) {
            inputView_init = XueYaLayoutInit.getInstance(this);
            setTime(TimeUtil.getTime(System.currentTimeMillis()));
        } else if (mode == BluetoothLeService.MODE_XUEYANG) {
            inputView_init = XueYangLayoutInit.getInstance(this);
        } else {
            inputView_init = XueTangLayoutInit.getInstance(this);
            setTime(TimeUtil.getTime(System.currentTimeMillis()));
        }
        inputLayout.addView(inputView_init.getInput_layout());
    }

    @Override
    protected void eventInit() {
        if (mode == BluetoothLeService.MODE_XUEYA) {
            inputView_init.getInput_layout().findViewById(R.id.time).setOnClickListener(this);

        } else if (mode == BluetoothLeService.MODE_XUETANG) {
            inputView_init.getInput_layout().findViewById(R.id.time).setOnClickListener(this);
            inputView_init.getInput_layout().findViewById(R.id.time_state).setOnClickListener(this);
        }
        submit.setOnClickListener(this);
    }

    //----------------View------------
    @Override
    public void showProgress() {
        getProgress().show();
    }

    @Override
    public void hideProgress() {
        getProgress().dismiss();
    }

    @Override
    public void error(String err) {
        submit.setEnabled(true);
        showToast(err);
    }

    @Override
    public void success(String msg) {
        showToast(msg);
        finish();
    }

    @Override
    public String getTime() {
        return ((TextView) inputView_init.getInput_layout().findViewById(R.id.time)).getText().toString();
    }

    @Override
    public void setTime(String time) {
        ((TextView) inputView_init.getInput_layout().findViewById(R.id.time)).setText(time);
    }

    @Override
    public String getShousuo() {
        return ((EditText) inputView_init.getInput_layout().findViewById(R.id.shosuo)).getText().toString();
    }

    @Override
    public String getShuzhang() {
        return ((EditText) inputView_init.getInput_layout().findViewById(R.id.shuzhang)).getText().toString();
    }

    @Override
    public String getMaibo() {
        return ((EditText) inputView_init.getInput_layout().findViewById(R.id.maibo)).getText().toString();
    }

    @Override
    public String getXueyang() {
        return ((EditText) inputView_init.getInput_layout().findViewById(R.id.xueyang)).getText().toString();
    }

    @Override
    public String getTimeState() {
        return ((TextView) inputView_init.getInput_layout().findViewById(R.id.time_state)).getText().toString();
    }

    @Override
    public void setTimeState(String time_state) {
        ((TextView) inputView_init.getInput_layout().findViewById(R.id.time_state)).setText(time_state);
    }

    @Override
    public String getXuetang() {
        return ((EditText) inputView_init.getInput_layout().findViewById(R.id.xuetang)).getText().toString();
    }

    //----------------Click---------
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.time:
                showTimePopup(view);
                break;
            case R.id.time_state:
                showTimeStatePopup(view);
                break;
            case R.id.item_1:
                setTimeState("空腹");
                time_state_popup.dismiss();
                break;
            case R.id.item_2:
                setTimeState("饭后2小时");
                time_state_popup.dismiss();
                break;
            case R.id.submit:
                view.setEnabled(false);
                if (mode == BluetoothLeService.MODE_XUEYA) {
                    presenter.doXueyaSave();
                } else if (mode == BluetoothLeService.MODE_XUEYANG) {
                    presenter.doXueyangSave();
                } else {
                    presenter.doXuetangSave();
                }
                break;
        }
    }

    //-----------------popup-------------
    private void showTimePopup(View view) {
        View contentView = getLayoutInflater().inflate(R.layout.time_picker_layout, null);
        final NumberPicker numberPicker_year = (NumberPicker) contentView
                .findViewById(R.id.popwindow_date_year);
        final NumberPicker numberPicker_month = (NumberPicker) contentView
                .findViewById(R.id.popwindow_date_month);
        final NumberPicker numberPicker_day = (NumberPicker) contentView
                .findViewById(R.id.popwindow_date_day);
        final NumberPicker numberPicker_hour = (NumberPicker) contentView
                .findViewById(R.id.popwindow_date_hour);
        final NumberPicker numberPicker_minute = (NumberPicker) contentView
                .findViewById(R.id.popwindow_date_minute);
        TextView view_save = (TextView) contentView
                .findViewById(R.id.popwindow_save_btn);
        view_save.setOnClickListener(arg0 -> {
            String year = numberPicker_year.getValue() + "";
            String month = numberPicker_month.getValue() + "";
            if (month.length() < 2) {
                month = "0" + month;
            }
            String day = numberPicker_day.getValue() + "";
            if (day.length() < 2) {
                day = "0" + day;
            }
            String hour = numberPicker_hour.getValue() + "";
            if (hour.length() < 2) {
                hour = "0" + hour;
            }
            String minute = numberPicker_minute.getValue() + "";
            if (minute.length() < 2) {
                minute = "0" + minute;
            }
            setTime(year + "-" + month + "-" + day
                    + " " + hour + ":" + minute);
            time = TimeUtil.dateFormat(year + month + day + hour + minute
                    + "00");
            time_popup.dismiss();
        });
        numberPicker_year.setMinValue(1970);
        numberPicker_year.setMaxValue(2100);
        numberPicker_year
                .setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker_year.setValue(Integer.parseInt(TimeUtil.getYear(System
                .currentTimeMillis())));
        numberPicker_month.setMinValue(1);
        numberPicker_month.setMaxValue(12);
        numberPicker_month
                .setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker_month.setValue(Integer.parseInt(TimeUtil.getMonth(System
                .currentTimeMillis())));
        numberPicker_day.setMinValue(1);
        numberPicker_day.setMaxValue(31);
        numberPicker_day
                .setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker_day.setValue(Integer.parseInt(TimeUtil.getDay(System
                .currentTimeMillis())));
        numberPicker_hour.setMinValue(0);
        numberPicker_hour.setMaxValue(23);
        numberPicker_hour
                .setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker_hour.setValue(Integer.parseInt(TimeUtil.getHour(System
                .currentTimeMillis())));
        numberPicker_minute.setMinValue(0);
        numberPicker_minute.setMaxValue(59);
        numberPicker_minute
                .setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker_minute.setValue(Integer.parseInt(TimeUtil.getMinute(System
                .currentTimeMillis())));
        contentView.setOnTouchListener((arg0, arg1) -> {
            // TODO Auto-generated method stub
            if (time_popup != null && time_popup.isShowing()) {
                time_popup.dismiss();
                time_popup = null;
                return true;
            }
            return false;
        });
        time_popup = PopupUtil.Builder()
                .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                .setContentView(contentView)
                .setBackgroundDrawable(new ColorDrawable(0x00000000))
                .setOutsideTouchable(false)
                .setFocusable(true)
                .showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    private void showTimeStatePopup(View view) {
        View contentView = getLayoutInflater().inflate(R.layout.xuetang_popup_layout, null);
        LinearLayout item_1 = contentView.findViewById(R.id.item_1);
        LinearLayout item_2 = contentView.findViewById(R.id.item_2);
        item_1.setOnClickListener(this);
        item_2.setOnClickListener(this);
        time_state_popup = PopupUtil.Builder()
                .setWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setContentView(contentView)
                .setBackgroundDrawable(new ColorDrawable(0x00000000))
                .setOutsideTouchable(false)
                .setFocusable(true)
                .showAsDropDown(view);
    }
}
