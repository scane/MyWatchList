package com.scanba.mywatchlist.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.scanba.mywatchlist.activities.MovieDetailsActivity;
import com.scanba.mywatchlist.models.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.sql.SQLException;
import java.util.ArrayList;


public class MyWatchListAdapter extends RecyclerView.Adapter<MyWatchListAdapter.MyViewHolder> {

    LayoutInflater layoutInflater;
    ArrayList<Movie> movies;
    Context context;
    Dao<Movie, Integer> movieDao;

    public MyWatchListAdapter(Context context, ArrayList<Movie> movies) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.movies = movies;
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        try {
            movieDao = databaseHelper.getMovieDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, MenuItem.OnMenuItemClickListener, View.OnCreateContextMenuListener {

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
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            Movie movie = movies.get(getAdapterPosition());
            Intent intent = new Intent(context, MovieDetailsActivity.class);
            intent.putExtra("MOVIE_ID", movie.getTheMovieDbId());
            context.startActivity(intent);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int position = item.getOrder();
            Movie movie = movies.get(position);
            switch (item.getTitle().toString()) {
                case "Delete":
                    AlertDialog alertDialog = getDeleteConfirmationDialog(context, movie, position);
                    alertDialog.show();
            }
            return true;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem item = menu.add(0, 0, getAdapterPosition(), "Delete");
            item.setOnMenuItemClickListener(this);
        }

        private AlertDialog getDeleteConfirmationDialog(Context context, final Movie movie, int position)
        {
            final int pos = position;
            final Context ctxt = context;
            AlertDialog deleteDialogBox = new AlertDialog.Builder(context)
                    .setView(layoutInflater.inflate(R.layout.delete_dialog, null))
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            movie.delete(movieDao);
                            movies.remove(pos);
                            notifyItemRemoved(pos);
                            notifyItemRangeChanged(pos, movies.size());
                            Toast.makeText(ctxt, "Movie deleted successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            return deleteDialogBox;

        }
    }

}
