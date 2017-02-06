package com.scanba.mywatchlist.fragments;


import android.widget.ProgressBar;

import com.scanba.mywatchlist.tasks.FetchMoviesTask;

public class TopRatedMoviesTabFragment extends BaseMoviesTapFragment {

    public TopRatedMoviesTabFragment() {
        // Required empty public constructor
    }

    @Override
    public String getMovieType() {
        return "top_rated";
    }
}
