package com.aiminerva.oldpeople.ui.examination;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.aiminerva.oldpeople.R;
import com.aiminerva.oldpeople.ui.examination.bean.MenuBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2017/6/10.
 */

public class ExaminationAdapter extends BaseQuickAdapter<MenuBean, BaseViewHolder> {
    public ExaminationAdapter(@LayoutRes int layoutResId, @Nullable List<MenuBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MenuBean item) {
        helper.setText(R.id.item_name, item.getName());
        helper.setImageResource(R.id.item_img, item.getImg());
    }
}
