package com.aiminerva.oldpeople.ui.hand.inputlayouts;

import android.content.Context;
import android.view.LayoutInflater;

import com.aiminerva.oldpeople.R;

/**
 * Created by Administrator on 2017/7/4.
 */

public class XueYaLayoutInit extends BaseInputView {
    private static XueYaLayoutInit xueYaLayoutInit = null;

    public XueYaLayoutInit(Context context) {
        input_layout = LayoutInflater.from(context).inflate(R.layout.hand_xueya, null);
    }

    public static synchronized XueYaLayoutInit getInstance(Context context) {
        if (xueYaLayoutInit == null) {
            xueYaLayoutInit = new XueYaLayoutInit(context);
        }
        return xueYaLayoutInit;
    }

//    public TextView getTimeView() {
//        return input_layout.findViewById(R.id.time);
//    }
//
//    public void setTimeView(String time) {
//        getTimeView().setText(time);
//    }
//
//    public EditText getShousuo() {
//        return input_layout.findViewById(R.id.shosuo);
//    }
//
//    public EditText getShuzhang() {
//        return input_layout.findViewById(R.id.shuzhang);
//    }
//
//    public EditText getMaibo() {
//        return input_layout.findViewById(R.id.maibo);
//    }

    @Override
    public void release() {
        xueYaLayoutInit = null;
        input_layout = null;
    }
}
