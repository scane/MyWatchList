package com.scanba.mywatchlist.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.scanba.mywatchlist.fragments.PopularMoviesTabFragment;
import com.scanba.mywatchlist.fragments.TopRatedMoviesTabFragment;
import com.scanba.mywatchlist.fragments.UpcomingMoviesTabFragment;

public class MoviesViewPagerAdapter extends FragmentStatePagerAdapter {
    String[] titles;

    public MoviesViewPagerAdapter(FragmentManager fm, String[] titles) {
        super(fm);
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        if (position == 0) {
            fragment =  new UpcomingMoviesTabFragment();
        } else if (position == 1) {
            fragment = new TopRatedMoviesTabFragment();
        } else {
            fragment = new PopularMoviesTabFragment();
        }
        return fragment;
    }

    @Override
    public String getPageTitle(int pos) {
        return titles[pos];
    }

    @Override
    public int getCount() {
        return 3;
    }
}
