package com.khokhlov.khokhlovart.price_watcher;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Dom on 03.12.2017.
 */

public class ItemPagerAdapter  extends FragmentPagerAdapter {

    private String[] titels;
    public static Item item;
    public ItemPagerAdapter(FragmentManager fm, Resources res, Item item) {
        super(fm);
        titels = res.getStringArray(R.array.tabs_name);
        this.item = item;
    }

    public Item getItem()
    {
        return item;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                itemInfoFragment f = new itemInfoFragment();
                return f;
            }
            case 1: {
                itemGraphFragment f = new itemGraphFragment();
                return f;
            }
            default:
                return new ErrorFragment();
        }
    }

    @Override
    public int getCount() {
        return titels.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titels[position];
    }
}
