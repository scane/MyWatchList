package com.scanba.mywatchlist.tasks;

import android.app.DownloadManager;
import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.scanba.mywatchlist.Constants;
import com.scanba.mywatchlist.VolleySingleton;
import com.scanba.mywatchlist.callbacks.MoviesLoadedListener;
import com.scanba.mywatchlist.models.Movie;
import com.scanba.mywatchlist.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

    private MoviesLoadedListener listener;
    private Context context;
    private String fetchType; //Can be either more or new or search

    public FetchMoviesTask(MoviesLoadedListener listener, Context context, String fetchType) {
        this.listener = listener;
        this.context = context;
        this.fetchType = fetchType;
    }

    @Override
    protected ArrayList<Movie> doInBackground(String... params) {
        JSONObject response = fetchMovies(params[0]);
        ArrayList<Movie> movies = new ArrayList<>();
        if(response != null)
            movies = parseMoviesJSON(response);
        return movies;
    }

    protected JSONObject fetchMovies(String urlPath) {
        JSONObject response = null;
        RequestQueue requestQueue = VolleySingleton.getInstance(context).getRequestQueue();
        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();
        String url = Constants.THE_MOVIE_DB_API_URL + urlPath +"&api_key="+Constants.THE_MOVIE_DB_API_KEY;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, requestFuture, requestFuture);
        requestQueue.add(jsonObjectRequest);
        try {
            response = requestFuture.get(10000, TimeUnit.MILLISECONDS);
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
    protected void onPostExecute(ArrayList<Movie> movies) {
        listener.onMoviesLoaded(movies, fetchType);
    }

    protected ArrayList<Movie> parseMoviesJSON(JSONObject response) {
        ArrayList<Movie> movies = new ArrayList<>();
        try {
            JSONArray moviesJSON = response.getJSONArray("results");
            String id, title, releaseDate, posterPath;
            int rating;
            for(int i =0; i < moviesJSON.length(); i++) {
                id = null;
                title = null;
                releaseDate = Constants.NA;
                posterPath = Constants.NA;
                rating = -1;
                JSONObject jsonObject = moviesJSON.getJSONObject(i);
                if(JSONParser.hasKey(jsonObject, "id"))
                    id = jsonObject.getString("id");
                if(JSONParser.hasKey(jsonObject, "title"))
                    title = jsonObject.getString("title");
                if(JSONParser.hasKey(jsonObject, "release_date"))
                    releaseDate = jsonObject.getString("release_date");
                if(JSONParser.hasKey(jsonObject, "poster_path"))
                    posterPath = jsonObject.getString("poster_path");
                if(JSONParser.hasKey(jsonObject, "vote_average"))
                    rating = jsonObject.getInt("vote_average")/2;

                if(id != null && title != null) {
                    Movie movie = new Movie();
                    movie.setTheMovieDbId(id);
                    movie.setTitle(title);
                    movie.setReleaseDate(releaseDate);
                    movie.setPosterPath(posterPath);
                    movie.setRating(rating);
                    movies.add(movie);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }
}
