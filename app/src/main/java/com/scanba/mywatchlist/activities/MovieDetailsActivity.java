package com.scanba.mywatchlist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.scanba.mywatchlist.Constants;
import com.scanba.mywatchlist.R;
import com.scanba.mywatchlist.callbacks.MovieLoadedListener;
import com.scanba.mywatchlist.models.Movie;
import com.scanba.mywatchlist.tasks.FetchMovieTask;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity implements MovieLoadedListener {

    TextView movieTitle, movieReleaseDate, movieGenres, movieDescription;
    ImageView moviePoster;
    RatingBar movieRating;
    ProgressBar movieLoader;

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

        movieLoader.setVisibility(ProgressBar.VISIBLE);

        FetchMovieTask fetchMovieTask = new FetchMovieTask(this, this);
        fetchMovieTask.execute(getIntent().getStringExtra("MOVIE_ID"));

    }

    @Override
    public void onMovieLoaded(Movie movie) {
        movieLoader.setVisibility(ProgressBar.INVISIBLE);
        String posterPath = movie.getPosterPath();
        if(posterPath != null) {
            Picasso.with(this).load(Constants.THE_MOVIE_DB_POSTER_URL + "w154" + posterPath + "?api_key" + Constants.THE_MOVIE_DB_API_KEY).into(moviePoster);
        }
        movieTitle.setText(movie.getTitle());
        movieReleaseDate.setText(movie.getReleaseDate());
        movieGenres.setText(movie.getGenres());
        movieDescription.setText(movie.getDescription());
        movieRating.setRating(movie.getRating());

    }

    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(this, MainActivity.class);
        startActivity(setIntent);
    }
}
