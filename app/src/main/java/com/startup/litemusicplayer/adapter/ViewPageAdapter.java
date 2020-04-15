package com.startup.litemusicplayer.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPageAdapter extends FragmentPagerAdapter {


    ArrayList<Fragment> fragmentArrayList = new ArrayList<Fragment>();
    ArrayList<String> stringArrayList = new ArrayList<String>();

    public ViewPageAdapter(@NonNull FragmentManager fm) {
        super(fm);

    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return stringArrayList.get(position);
    }

    public void addFragment(Fragment fragment, String string) {
        fragmentArrayList.add(fragment);
        stringArrayList.add(string);

    }

}
