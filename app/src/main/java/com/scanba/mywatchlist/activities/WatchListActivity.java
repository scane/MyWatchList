package com.scanba.mywatchlist.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.scanba.mywatchlist.DatabaseHelper;
import com.scanba.mywatchlist.R;
import com.scanba.mywatchlist.adapters.MoviesListAdapter;
import com.scanba.mywatchlist.models.Movie;

import java.sql.SQLException;
import java.util.ArrayList;

public class WatchListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private MoviesListAdapter adapter;
    private Dao<Movie, Integer> movieDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        ArrayList<Movie> movies = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.movies_list);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        try {
            movieDao = databaseHelper.getMovieDao();
            movies = (ArrayList<Movie>) movieDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        adapter = new MoviesListAdapter(this, movies);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

    }
}
