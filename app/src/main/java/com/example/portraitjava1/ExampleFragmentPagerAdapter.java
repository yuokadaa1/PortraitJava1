package com.example.portraitjava1;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ExampleFragmentPagerAdapter extends FragmentPagerAdapter {
    public ExampleFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
//        switch (position) {
//            case 0:
//                return ExampleFragment.newInstance(android.R.color.holo_blue_bright);
//            case 1:
//                return ExampleFragment.newInstance(android.R.color.holo_green_light);
//            case 2:
//                return ExampleFragment.newInstance(android.R.color.holo_red_dark);
//        }
        if (position <= GlobalVariable.getgPhotoCount()){
            return ExampleFragment.newInstance(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        return GlobalVariable.getgPhotoCount();
//        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "ページ" + (position + 1);
    }
}