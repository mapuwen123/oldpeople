package com.aiminerva.oldpeople.utils;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.PopupWindow;

/**
 * Created by Administrator on 2017/7/4.
 */

public class PopupUtil {
    private static PopupUtil popupUtil = null;

    private PopupWindow popup = null;

    public PopupUtil() {
        popup = new PopupWindow();
    }

    public static synchronized PopupUtil Builder() {
        if (popupUtil == null) {
            popupUtil = new PopupUtil();
        }
        return popupUtil;
    }

    public PopupUtil setWidth(int width) {
        popup.setWidth(width);
        return popupUtil;
    }

    public PopupUtil setHeight(int height) {
        popup.setHeight(height);
        return popupUtil;
    }

    public PopupUtil setContentView(View contentView) {
        popup.setContentView(contentView);
        return popupUtil;
    }

    public PopupUtil setBackgroundDrawable(Drawable background) {
        popup.setBackgroundDrawable(background);
        return popupUtil;
    }

    public PopupUtil setOutsideTouchable(boolean touchable) {
        popup.setOutsideTouchable(touchable);
        return popupUtil;
    }

    public PopupUtil setFocusable(boolean focusable) {
        popup.setFocusable(focusable);
        return popupUtil;
    }

    public PopupWindow showAsDropDown(View anchor) {
        popup.showAsDropDown(anchor);
        return popup;
    }

    public PopupWindow showAtLocation(View parent, int gravity, int x, int y) {
        popup.showAtLocation(parent, gravity, x, y);
        return popup;
    }
}
