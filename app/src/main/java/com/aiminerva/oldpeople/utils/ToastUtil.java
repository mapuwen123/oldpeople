package com.aiminerva.oldpeople.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aiminerva.oldpeople.R;

/**
 * Toast统一管理类
 * Created by mapuw on 15/12/27.
 */
public class ToastUtil {

    private static ToastUtil toastUtil = null;

    private Toast toast;
    private View view;
    private TextView toast_view;

    private ToastUtil(Context context) {
        view = View.inflate(context, R.layout.toast_layout, null);
        toast_view = view.findViewById(R.id.toast);
        toast = new Toast(context);
        toast.setView(view);
        toast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
        toast.setMargin(0, 0);
    }

    public static boolean isShow = true;

    public static synchronized ToastUtil getInstance(Context context) {
        if (toastUtil == null) {
            toastUtil = new ToastUtil(context);
        }
        return toastUtil;
    }

    public ToastUtil setDuration(int duratuion) {
        toast.setDuration(duratuion);
        return toastUtil;
    }

    public ToastUtil setText(CharSequence s) {
        toast_view.setText(s);
        return toastUtil;
    }

    public void show() {
        toast.show();
    }
}
