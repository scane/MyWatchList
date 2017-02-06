package com.scanba.mywatchlist.callbacks;


import com.scanba.mywatchlist.models.Movie;

import java.util.ArrayList;


public interface MoviesLoadedListener {
    void onMoviesLoaded(ArrayList<Movie> movies, String fetchType);
}
