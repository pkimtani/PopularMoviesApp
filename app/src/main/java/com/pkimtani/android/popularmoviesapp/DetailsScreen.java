package com.pkimtani.android.popularmoviesapp;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.pkimtani.android.popularmoviesapp.data.DBContract.MovieEntry;
import com.squareup.picasso.Picasso;


public class DetailsScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        long id = Long.parseLong(getIntent().getStringExtra(PopularMoviesList.MOVIE_ID));

        String mTitle = getIntent().getStringExtra(PopularMoviesList.MOVIE_TITLE);
        String mOverview = getIntent().getStringExtra(PopularMoviesList.MOVIE_OVERVIEW);

        TextView movie_title = (TextView) findViewById(R.id.movie_title);
        TextView movie_overview = (TextView) findViewById(R.id.movie_overview);
        TextView vote = (TextView) findViewById(R.id.vote_avg);
        TextView release = (TextView) findViewById(R.id.release_date);

        ImageView poster = (ImageView) findViewById(R.id.movie_poster);

        movie_title.setText(mTitle);
        movie_overview.setText(mOverview);

        Uri uri = MovieEntry.buildMovieUriFromId(id);

        Log.i(HomeScreen.LOG_TAG, uri.toString());

        Cursor cursor = getContentResolver().query(
                uri,
                new String[] {MovieEntry.MOVIE_IMG_PATH, MovieEntry.MOVIE_VOTE_AVG, MovieEntry.MOVIE_RELEASE_DATE},
                null,
                null,
                null
        );

        cursor.moveToFirst();

        vote.setText("Rating: " + cursor.getString(cursor.getColumnIndex(MovieEntry.MOVIE_VOTE_AVG)));
        release.setText("Release Date: " + cursor.getString(cursor.getColumnIndex(MovieEntry.MOVIE_RELEASE_DATE)));

        Picasso.with(this).load("http://image.tmdb.org/t/p/w185" + cursor.getString(cursor.getColumnIndex(MovieEntry.MOVIE_IMG_PATH))).into(poster);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cursor.close();
    }

}
