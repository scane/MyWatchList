package com.scanba.mywatchlist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.scanba.mywatchlist.Constants;
import com.scanba.mywatchlist.DatabaseHelper;
import com.scanba.mywatchlist.R;
import com.scanba.mywatchlist.callbacks.MovieLoadedListener;
import com.scanba.mywatchlist.models.Movie;
import com.scanba.mywatchlist.tasks.FetchMovieTask;
import com.squareup.picasso.Picasso;

import java.sql.SQLException;

public class MovieDetailsActivity extends AppCompatActivity implements MovieLoadedListener {

    TextView movieTitle, movieReleaseDate, movieGenres, movieDescription;
    ImageView moviePoster;
    RatingBar movieRating;
    ProgressBar movieLoader;
    Button addToWatchListButton;
    Movie movie;
    DatabaseHelper databaseHelper;
    Dao<Movie, Integer> movieDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        movieTitle = (TextView) findViewById(R.id.movie_title);
        movieReleaseDate = (TextView) findViewById(R.id.movie_release_date);
        movieGenres = (TextView) findViewById(R.id.movie_genres);
        movieDescription = (TextView) findViewById(R.id.movie_description);
        moviePoster = (ImageView) findViewById(R.id.movie_poster);
        movieRating = (RatingBar) findViewById(R.id.movie_rating);
        movieLoader = (ProgressBar) findViewById(R.id.movie_loader);
        addToWatchListButton = (Button) findViewById(R.id.add_to_watchlist_button);

        databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        try {
            movieDao = databaseHelper.getMovieDao();
            String theMovieDbId = getIntent().getStringExtra("MOVIE_ID");
            FetchMovieTask fetchMovieTask = new FetchMovieTask(this, this);
            fetchMovieTask.execute(theMovieDbId);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMovieLoaded(Movie movie) {
        if(movie != null) {
            this.movie = movie;
            movieLoader.setVisibility(ProgressBar.INVISIBLE);
            String posterPath = movie.getPosterPath();
            if(posterPath != null) {
                Picasso.with(this).load(Constants.THE_MOVIE_DB_POSTER_URL + "w154" + posterPath + "?api_key" + Constants.THE_MOVIE_DB_API_KEY)
                        .into(moviePoster);
            }
            movieTitle.setText(movie.getTitle());
            movieReleaseDate.setText(movie.getReleaseDate());
            movieGenres.setText(movie.getGenres());
            movieDescription.setText(movie.getDescription());
            movieRating.setRating(movie.getRating());
            movieRating.setVisibility(RatingBar.VISIBLE);
            if(movieDao != null && !Movie.exists(movie.getTheMovieDbId(), movieDao)) {
                addToWatchListButton.setVisibility(Button.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(this, MainActivity.class);
        startActivity(setIntent);
    }

    public void addToWatchList(View view) {
        addToWatchListButton.setVisibility(View.INVISIBLE);
        try {
            movieDao.create(movie);
            Toast.makeText(this, "Successfully added movie to watch list.", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to add to watch list. Please try again.", Toast.LENGTH_SHORT).show();
            addToWatchListButton.setVisibility(Button.VISIBLE);
        }
    }
}
