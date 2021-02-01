package com.example.chanyoung.se;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    private int tabCount;
    FragmentManager fm;

    Home_Fragment home_fragment;
    ProductList_Fragment productlist_fragment;
    Friends_Fragment friends_fragment;
    Border_Fragment border_fragment;

    public TabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
        this.fm = fm;

        home_fragment = new Home_Fragment();
        productlist_fragment = new ProductList_Fragment();
        border_fragment = new Border_Fragment();
        friends_fragment = new Friends_Fragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return home_fragment;
            case 1:
                return productlist_fragment;
            case 2:
                return border_fragment;
            case 3:
                return friends_fragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
