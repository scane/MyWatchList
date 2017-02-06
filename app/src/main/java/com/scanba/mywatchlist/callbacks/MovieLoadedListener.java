package com.scanba.mywatchlist.callbacks;

import com.scanba.mywatchlist.models.Movie;

/**
 * Created by Scanny on 05/02/17.
 */

public interface MovieLoadedListener {
    void onMovieLoaded(Movie movie);
}
