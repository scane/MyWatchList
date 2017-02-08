package com.scanba.mywatchlist.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.scanba.mywatchlist.Constants;
import com.scanba.mywatchlist.R;
import com.scanba.mywatchlist.activities.MovieDetailsActivity;
import com.scanba.mywatchlist.models.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MoviesListAdapter extends RecyclerView.Adapter<MoviesListAdapter.MyViewHolder> {

    LayoutInflater layoutInflater;
    ArrayList<Movie> movies;
    Context context;

    public MoviesListAdapter(Context context, ArrayList<Movie> movies) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.movies = movies;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.movie_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Movie movie = movies.get(position);
        Picasso.with(context).load(getPosterURL(movie)).into(holder.moviePoster, new Callback() {
            @Override
            public void onSuccess() {
                holder.moviePosterLoader.setVisibility(ProgressBar.INVISIBLE);
            }
            @Override
            public void onError() {

            }
        });
        holder.movieTitle.setText(movie.getTitle());
        holder.movieReleaseDate.setText(movie.getReleaseDate());
        holder.movieRating.setRating(movie.getRating());
    }

    private String getPosterURL(Movie movie) {
        return Constants.THE_MOVIE_DB_POSTER_URL + "w92" + movie.getPosterPath();
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public void addMovies(ArrayList<Movie> movies) {
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView moviePoster;
        TextView movieTitle, movieReleaseDate;
        RatingBar movieRating;
        ProgressBar moviePosterLoader;

        public MyViewHolder(View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.movie_poster);
            movieTitle = (TextView) itemView.findViewById(R.id.movie_title);
            movieReleaseDate = (TextView) itemView.findViewById(R.id.movie_release_date);
            movieRating = (RatingBar) itemView.findViewById(R.id.movie_rating);
            moviePosterLoader = (ProgressBar) itemView.findViewById(R.id.movie_poster_loader);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Movie movie = movies.get(getAdapterPosition());
            Intent intent = new Intent(context, MovieDetailsActivity.class);
            intent.putExtra("MOVIE_ID", movie.getTheMovieDbId());
            context.startActivity(intent);
        }
    }

}
