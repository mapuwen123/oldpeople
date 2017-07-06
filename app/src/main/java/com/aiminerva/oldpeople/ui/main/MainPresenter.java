package com.aiminerva.oldpeople.ui.main;

import com.aiminerva.oldpeople.base.BasePresenter;
import com.aiminerva.oldpeople.base.OnPresenterListener;

/**
 * Created by Administrator on 2017/6/12.
 */

public class MainPresenter extends BasePresenter<MainView> implements OnPresenterListener<String> {
    private MainModel mainModel;

    public MainPresenter() {
        mainModel = new MainModel();
    }
    @Override
    public void success(String msg) {

    }

    @Override
    public void error(String err) {

    }
}
