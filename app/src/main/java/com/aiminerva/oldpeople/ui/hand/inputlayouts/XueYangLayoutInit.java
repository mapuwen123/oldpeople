package com.aiminerva.oldpeople.ui.hand.inputlayouts;

import android.content.Context;
import android.view.LayoutInflater;

import com.aiminerva.oldpeople.R;

/**
 * Created by Administrator on 2017/7/4.
 */

public class XueYangLayoutInit extends BaseInputView {
    private static XueYangLayoutInit xueYangLayoutInit = null;

    public XueYangLayoutInit(Context context) {
        input_layout = LayoutInflater.from(context).inflate(R.layout.hand_xueyang, null);
    }

    public static synchronized XueYangLayoutInit getInstance(Context context) {
        if (xueYangLayoutInit == null) {
            xueYangLayoutInit = new XueYangLayoutInit(context);
        }
        return xueYangLayoutInit;
    }

//    public EditText getXueyang() {
//        return input_layout.findViewById(R.id.xueyang);
//    }
//
//    public EditText getMaibo() {
//        return input_layout.findViewById(R.id.maibo);
//    }

    @Override
    public void release() {
        xueYangLayoutInit = null;
        input_layout = null;
    }
}
