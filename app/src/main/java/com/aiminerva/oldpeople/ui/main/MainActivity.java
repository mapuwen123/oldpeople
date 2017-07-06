package com.aiminerva.oldpeople.ui.main;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.aiminerva.oldpeople.R;
import com.aiminerva.oldpeople.base.BaseActivity;
import com.aiminerva.oldpeople.ui.examination.ExaminationFragment;
import com.aiminerva.oldpeople.ui.inquiry.InquiryFragment;
import com.aiminerva.oldpeople.utils.ActivityUtils;

import butterknife.BindView;

public class MainActivity extends BaseActivity<MainView, MainPresenter> implements MainView,
        InquiryFragment.OnFragmentInteractionListener,
        ExaminationFragment.OnFragmentInteractionListener,
        BottomNavigationView.OnNavigationItemSelectedListener,
        ViewPager.OnPageChangeListener {

    @BindView(R.id.bottombar)
    BottomNavigationView bottombar;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private MainPagerFragmentAdapter adapter;

    private MenuItem menuItem;

    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.ask_normal));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public MainPresenter initPresenter() {
        return new MainPresenter();
    }

    @Override
    protected void dataInit() {
        adapter = new MainPagerFragmentAdapter(getSupportFragmentManager());
        adapter.add(InquiryFragment.newInstance(getResources().getString(R.string.ask_normal)));
        adapter.add(ExaminationFragment.newInstance(getResources().getString(R.string.physicals_normal)));

        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(0);
    }

    @Override
    protected void eventInit() {
        bottombar.setOnNavigationItemSelectedListener(this);
        viewpager.addOnPageChangeListener(this);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.inquiry_item:
                viewpager.setCurrentItem(0);
                setTitle(getResources().getString(R.string.ask_normal));
                break;
            case R.id.examination_item:
                viewpager.setCurrentItem(1);
                setTitle(getResources().getString(R.string.physicals_normal));
                break;
        }
        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                setTitle(getResources().getString(R.string.ask_normal));
                break;
            case 1:
                setTitle(getResources().getString(R.string.physicals_normal));
                break;
        }
        if (menuItem != null) {
            menuItem.setChecked(false);
        } else {
            bottombar.getMenu().getItem(0).setChecked(false);
        }
        menuItem = bottombar.getMenu().getItem(position);
        menuItem.setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            exitTime = System.currentTimeMillis();
            showToast("再按一次退出");
            return;
        } else {
            ActivityUtils.removeAllActivity();
        }
        super.onBackPressed();
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void error(String err) {

    }
}
