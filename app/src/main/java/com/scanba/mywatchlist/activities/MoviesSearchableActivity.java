package com.scanba.mywatchlist.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Switch;

public class MoviesSearchableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        switch (intent.getAction()) {
            case Intent.ACTION_SEARCH:
                Intent resultsIntent = new Intent(this, MoviesSearchResultsActivity.class);
                resultsIntent.putExtra(SearchManager.QUERY, intent.getStringExtra(SearchManager.QUERY));
                startActivity(resultsIntent);
                break;
            case Intent.ACTION_VIEW:
                Intent detailsIntent = new Intent(this, MovieDetailsActivity.class);
                detailsIntent.putExtra("MOVIE_ID", intent.getData().toString());
                startActivity(detailsIntent);
                break;

        }
    }
}
