package com.scanba.mywatchlist.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ProgressBar;

import com.scanba.mywatchlist.R;
import com.scanba.mywatchlist.adapters.MoviesListAdapter;
import com.scanba.mywatchlist.callbacks.MoviesLoadedListener;
import com.scanba.mywatchlist.models.Movie;
import com.scanba.mywatchlist.tasks.FetchMoviesTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MoviesSearchResultsActivity extends AppCompatActivity implements MoviesLoadedListener {

    protected RecyclerView recyclerView;
    protected LinearLayoutManager layoutManager;
    protected MoviesListAdapter moviesListAdapter;
    protected ArrayList<Movie> movies;
    protected FetchMoviesTask fetchMoviesTask;
    protected ProgressBar moviesLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_search_results);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        movies = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.movies_list);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        moviesListAdapter = new MoviesListAdapter(this, movies);
        recyclerView.setAdapter(moviesListAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        moviesLoader = (ProgressBar) findViewById(R.id.movies_loader);
        moviesLoader.setVisibility(ProgressBar.VISIBLE);

        Intent intent = getIntent();

        fetchMoviesTask = new FetchMoviesTask(this, this, "search");
        try {
            fetchMoviesTask.execute("search/movie?page=1&query=" + URLEncoder.encode(intent.getStringExtra(SearchManager.QUERY), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            moviesLoader.setVisibility(ProgressBar.INVISIBLE);
        }

    }

    @Override
    public void onMoviesLoaded(ArrayList<Movie> movies, String fetchType) {
        moviesLoader.setVisibility(ProgressBar.INVISIBLE);
        moviesListAdapter.setMovies(movies);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(this, MainActivity.class);
        startActivity(setIntent);
    }
}
