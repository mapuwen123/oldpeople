package com.aiminerva.oldpeople.ui.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/13.
 */

public class MainPagerFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> list;

    public MainPagerFragmentAdapter(FragmentManager fm) {
        super(fm);
        list = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void add(Fragment fragment) {
        list.add(fragment);
    }
}
