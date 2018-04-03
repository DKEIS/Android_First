package com.example.hyojong.dkeis;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by HyoJong on 2018-03-30.
 */

public class TabPagerAdapter extends FragmentPagerAdapter {
    private static int PAGE_NUMBER = 3;

    public TabPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return TransferFragment.newInstance();
            case 0:
                return SharingFragment.newInstance();
            case 2:
                return SettingFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 1:
                return "메인";
            case 0:
                return "공유";
            case 2:
                return "설정";
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return PAGE_NUMBER;
    }
}
