package com.aiminerva.oldpeople.ui.fat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aiminerva.oldpeople.R;
import com.aiminerva.oldpeople.base.BaseActivity;
import com.aiminerva.oldpeople.ui.newmain.NewMainActivity;

import butterknife.BindView;

public class FatActivity extends BaseActivity<FatView, FatPresenter> implements FatView,
        View.OnClickListener {
    @BindView(R.id.height)
    EditText height;
    @BindView(R.id.weight)
    EditText weight;
    @BindView(R.id.moisture)
    EditText moisture;
    @BindView(R.id.fat)
    EditText fat;
    @BindView(R.id.bim)
    EditText bim;
    @BindView(R.id.submit)
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(NewMainActivity.names[getIntent().getBundleExtra("INTENT").getInt("TITLE_TYPE")]);
        setBack(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fat;
    }

    @Override
    public FatPresenter initPresenter() {
        return new FatPresenter();
    }

    @Override
    protected void dataInit() {

    }

    @Override
    protected void eventInit() {
        submit.setOnClickListener(this);
    }

    //---------------View----------
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
    public int getHeight() {
        if (height.getText().toString().equalsIgnoreCase("")) {
            return 0;
        }
        return Integer.valueOf(height.getText().toString());
    }

    @Override
    public int getWeight() {
        if (weight.getText().toString().equalsIgnoreCase("")) {
            return 0;
        }
        return Integer.valueOf(weight.getText().toString());
    }

    @Override
    public int getMoisture() {
        if (moisture.getText().toString().equalsIgnoreCase("")) {
            return 0;
        }
        return Integer.valueOf(moisture.getText().toString());
    }

    @Override
    public int getFat() {
        if (fat.getText().toString().equalsIgnoreCase("")) {
            return 0;
        }
        return Integer.valueOf(fat.getText().toString());
    }

    @Override
    public int getBim() {
        if (bim.getText().toString().equalsIgnoreCase("")) {
            return 0;
        }
        return Integer.valueOf(bim.getText().toString());
    }

    @Override
    public void success(String msg) {
        showToast(msg);
        finish();
    }

    @Override
    public void onClick(View view) {
        submit.setEnabled(false);
        presenter.submitData();
    }
}
