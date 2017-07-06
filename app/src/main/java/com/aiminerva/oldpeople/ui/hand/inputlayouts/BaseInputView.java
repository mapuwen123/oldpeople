package com.aiminerva.oldpeople.ui.hand.inputlayouts;

import android.view.View;

/**
 * Created by Administrator on 2017/7/4.
 */

public abstract class BaseInputView {
    public View input_layout;

    public View getInput_layout() {
        return input_layout;
    }

    public abstract void release();
}
