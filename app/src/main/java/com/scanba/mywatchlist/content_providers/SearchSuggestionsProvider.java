package com.scanba.mywatchlist.content_providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.v7.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.scanba.mywatchlist.Constants;
import com.scanba.mywatchlist.VolleySingleton;
import com.scanba.mywatchlist.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static android.app.SearchManager.SUGGEST_COLUMN_ICON_1;
import static android.app.SearchManager.SUGGEST_COLUMN_INTENT_DATA;
import static android.app.SearchManager.SUGGEST_COLUMN_TEXT_1;
import static android.app.SearchManager.SUGGEST_COLUMN_TEXT_2;
import static android.provider.BaseColumns._ID;

public class SearchSuggestionsProvider extends ContentProvider {

    public static final String[] CURSOR_COLUMNS = {_ID, SUGGEST_COLUMN_TEXT_1, SUGGEST_COLUMN_TEXT_2, SUGGEST_COLUMN_INTENT_DATA};

    Context context;

    public SearchSuggestionsProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        context = getContext();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String query = uri.getLastPathSegment().toLowerCase();
        JSONObject response = searchMovies(query);
        return parseMovies(response);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private JSONObject searchMovies(String query) {
        JSONObject response = null;
        RequestQueue requestQueue = VolleySingleton.getInstance(context).getRequestQueue();
        try {
            String url = Constants.THE_MOVIE_DB_API_URL + "search/movie?api_key=" + Constants.THE_MOVIE_DB_API_KEY + "&query=" + URLEncoder.encode(query, "UTF-8");
            RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, requestFuture, requestFuture);
            requestQueue.add(request);
            response = requestFuture.get(Constants.THE_MOVIE_DB_API_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return response;
    }

    private MatrixCursor parseMovies(JSONObject response) {
        MatrixCursor matrixCursor = new MatrixCursor(CURSOR_COLUMNS);
        try {
            JSONArray jsonArray = response.getJSONArray("results");
            String id, title, releaseDate;
            int limit = jsonArray.length() > 8 ? 8 : jsonArray.length();
            for (int i = 0; i< limit; i++) {
                id = null;
                title = null;
                releaseDate = Constants.NA;
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(JSONParser.hasKey(jsonObject, "id"))
                    id = jsonObject.getString("id");
                if(JSONParser.hasKey(jsonObject, "title"))
                    title = jsonObject.getString("title");
                if(JSONParser.hasKey(jsonObject, "release_date"))
                    releaseDate = jsonObject.getString("release_date");
                if(id != null && title != null)
                    matrixCursor.addRow(new Object[] {id, title, releaseDate, id});
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return matrixCursor;
    }
}
