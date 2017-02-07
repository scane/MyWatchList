package com.scanba.mywatchlist.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.scanba.mywatchlist.R;
import com.scanba.mywatchlist.adapters.MoviesListAdapter;
import com.scanba.mywatchlist.callbacks.EndlessRecyclerViewScrollListener;
import com.scanba.mywatchlist.callbacks.MoviesLoadedListener;
import com.scanba.mywatchlist.models.Movie;
import com.scanba.mywatchlist.tasks.FetchMoviesTask;

import java.util.ArrayList;

public abstract class BaseMoviesTapFragment extends Fragment implements MoviesLoadedListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String MOVIES_SAVED_STATE_KEY = "movies_state_key";
    protected RecyclerView recyclerView;
    protected LinearLayoutManager layoutManager;
    protected MoviesListAdapter moviesListAdapter;
    protected ArrayList<Movie> movies;
    protected FetchMoviesTask fetchMoviesTask;
    protected SwipeRefreshLayout refreshMoviesLayout;
    protected ProgressBar moviesLoader;
    protected ProgressBar moreMoviesLoader;
    protected EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_movies_tab, container, false);
        moviesLoader = (ProgressBar) view.findViewById(R.id.movies_loader);
        moreMoviesLoader = (ProgressBar) view.findViewById(R.id.more_movies_loader);
        refreshMoviesLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_movies_layout);
        refreshMoviesLayout.setOnRefreshListener(this);
        refreshMoviesLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        recyclerView = (RecyclerView) view.findViewById(R.id.movies_list);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        movies = new ArrayList<>();
        moviesListAdapter = new MoviesListAdapter(getActivity(), movies);
        recyclerView.setAdapter(moviesListAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                fetchMovies(page, "more");
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        if(savedInstanceState != null) {
            movies = savedInstanceState.getParcelableArrayList(MOVIES_SAVED_STATE_KEY);
            moviesListAdapter.setMovies(movies);
        }
        else
            fetchMovies(1, "new");
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIES_SAVED_STATE_KEY, movies);
    }

    @Override
    public void onMoviesLoaded(ArrayList<Movie> movies, String fetchType) {
        if(fetchType.equals("new")) {
            moviesLoader.setVisibility(ProgressBar.INVISIBLE);
            moviesListAdapter.setMovies(movies);
            this.movies = movies;
        }
        else if(fetchType.equals("refresh")) {
            moviesListAdapter.setMovies(movies);
            refreshMoviesLayout.setRefreshing(false);
        }
        else {
            moreMoviesLoader.setVisibility(ProgressBar.INVISIBLE);
            moviesListAdapter.addMovies(movies);
        }
    }

    @Override
    public void onRefresh() {
        fetchMovies(1, "refresh");
    }

    public void fetchMovies(Integer pageNumber, String fetchType) {
        if(fetchType.equals("new"))
            moviesLoader.setVisibility(ProgressBar.VISIBLE);
        else if(fetchType.equals("more"))
            moreMoviesLoader.setVisibility(ProgressBar.VISIBLE);
        fetchMoviesTask = new FetchMoviesTask(this, getActivity(), fetchType);
        String urlPath = "movie/" + getMovieType() + "?page=" + pageNumber;
        fetchMoviesTask.execute(urlPath);
    }

    public abstract String getMovieType();
}
