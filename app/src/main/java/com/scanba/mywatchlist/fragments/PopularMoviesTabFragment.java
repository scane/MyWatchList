package com.scanba.mywatchlist.fragments;


import android.widget.ProgressBar;

import com.scanba.mywatchlist.tasks.FetchMoviesTask;

public class PopularMoviesTabFragment extends BaseMoviesTapFragment {

    public PopularMoviesTabFragment() {
        // Required empty public constructor
    }

    @Override
    public String getMovieType() {
        return "popular";
    }
}
