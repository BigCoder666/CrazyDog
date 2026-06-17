package com.example.datalibrary.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ActivitionPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<BaseFragment> mFragments = new ArrayList<>();

    public ActivitionPagerAdapter(@NonNull FragmentManager fm, ArrayList<BaseFragment> mFragments) {
        super(fm);
        this.mFragments = mFragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }


    // 原本ViewPager内存中缓存了Fragment，重写该方法使adapter重新创建新的Fragment
    @Override
    public long getItemId(int position) {
        return mFragments.get(position).hashCode();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
