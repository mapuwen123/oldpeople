package com.aiminerva.oldpeople.ui.main;

import com.aiminerva.oldpeople.base.BasePresenter;

/**
 * Created by Administrator on 2017/6/12.
 */

public class MainPresenter extends BasePresenter<MainView> {
    private MainModel mainModel;

    public MainPresenter() {
        mainModel = new MainModel();
    }
}
