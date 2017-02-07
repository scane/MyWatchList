package com.scanba.mywatchlist.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.scanba.mywatchlist.Constants;
import com.scanba.mywatchlist.VolleySingleton;
import com.scanba.mywatchlist.callbacks.MovieLoadedListener;
import com.scanba.mywatchlist.callbacks.MoviesLoadedListener;
import com.scanba.mywatchlist.models.Movie;
import com.scanba.mywatchlist.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FetchMovieTask extends AsyncTask<String, Void, Movie> {

    private MovieLoadedListener listener;
    private Context context;

    public FetchMovieTask(MovieLoadedListener listener, Context context) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected Movie doInBackground(String... params) {
        JSONObject response = fetchMovie(params[0]);
        Movie movie = null;
        if (response != null)
            movie = parseMoviesJSON(response);
        return movie;
    }

    protected JSONObject fetchMovie(String id) {
        JSONObject response = null;
        RequestQueue requestQueue = VolleySingleton.getInstance(context).getRequestQueue();
        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();
        String url = Constants.THE_MOVIE_DB_API_URL + "movie/" + id + "?api_key=" + Constants.THE_MOVIE_DB_API_KEY;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, requestFuture, requestFuture);
        requestQueue.add(jsonObjectRequest);
        try {
            response = requestFuture.get(Constants.THE_MOVIE_DB_API_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    protected void onPostExecute(Movie movie) {
        listener.onMovieLoaded(movie);
    }

    protected Movie parseMoviesJSON(JSONObject response) {
        Movie movie = null;
        try {
            String id = null, title = null, releaseDate = Constants.NA, posterPath = null;
            String description = Constants.NA, genres_string = Constants.NA;
            JSONArray genres;
            int rating = 1;

            if(JSONParser.hasKey(response, "id"))
                id = response.getString("id");
            if (JSONParser.hasKey(response, "title"))
                title = response.getString("title");
            if (JSONParser.hasKey(response, "release_date"))
                releaseDate = response.getString("release_date");
            if (JSONParser.hasKey(response, "poster_path"))
                posterPath = response.getString("poster_path");
            if (JSONParser.hasKey(response, "vote_average"))
                rating = response.getInt("vote_average") / 2;
            if (JSONParser.hasKey(response, "overview"))
                description = response.getString("overview");
            if (JSONParser.hasKey(response, "genres")) {
                genres = response.getJSONArray("genres");
                if(genres.length() > 0) {
                    genres_string = genres.getJSONObject(0).getString("name");
                    for(int i = 1; i < genres.length(); i++)
                        genres_string += ", " + genres.getJSONObject(i).getString("name");
                }
            }

            if (id != null && title != null) {
                movie = new Movie();
                movie.setTheMovieDbId(id);
                movie.setTitle(title);
                movie.setReleaseDate(releaseDate);
                movie.setPosterPath(posterPath);
                movie.setRating(rating);
                movie.setDescription(description);
                movie.setGenres(genres_string);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movie;
    }
}
