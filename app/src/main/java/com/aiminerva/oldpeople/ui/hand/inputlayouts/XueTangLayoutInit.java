package com.aiminerva.oldpeople.ui.hand.inputlayouts;

import android.content.Context;
import android.view.LayoutInflater;

import com.aiminerva.oldpeople.R;

/**
 * Created by Administrator on 2017/7/4.
 */

public class XueTangLayoutInit extends BaseInputView {
    private static XueTangLayoutInit xueTangLayoutInit = null;

    public XueTangLayoutInit(Context context) {
        input_layout = LayoutInflater.from(context).inflate(R.layout.hand_xuetang, null);
    }

    public static synchronized XueTangLayoutInit getInstance(Context context) {
        if (xueTangLayoutInit == null) {
            xueTangLayoutInit = new XueTangLayoutInit(context);
        }
        return xueTangLayoutInit;
    }

//    public TextView getTimeView() {
//        return input_layout.findViewById(R.id.time);
//    }
//
//    public void setTimeView(String time) {
//        getTimeView().setText(time);
//    }
//
//    public TextView getTimeStateView() {
//        return input_layout.findViewById(R.id.time_state);
//    }
//
//    public void setTimeStateView(String time_state) {
//        getTimeStateView().setText(time_state);
//    }
//
//    public EditText getXuetang() {
//        return input_layout.findViewById(R.id.xuetang);
//    }

    @Override
    public void release() {
        xueTangLayoutInit = null;
        input_layout = null;
    }
}
