package com.todo.order.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> arrayFragments;
    private ArrayList<String> arrayTitles;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior, ArrayList<Fragment> arrayFragments, ArrayList<String> arrayTitles) {
        super(fm, behavior);
        this.arrayFragments = arrayFragments;
        this.arrayTitles = arrayTitles;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return arrayFragments.get(position);
    }

    @Override
    public int getCount() {
        return arrayFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return arrayTitles.get(position);
    }
}
