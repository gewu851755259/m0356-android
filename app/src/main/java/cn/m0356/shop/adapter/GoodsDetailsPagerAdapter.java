package cn.m0356.shop.adapter;



import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by jiangtao on 2016/9/2.
 */
public class GoodsDetailsPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public GoodsDetailsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public GoodsDetailsPagerAdapter(FragmentManager fm, List<Fragment> list){
        this(fm);
        this.fragments = list;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
