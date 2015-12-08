package com.pkimtani.android.popularmoviesapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.pkimtani.android.popularmoviesapp.data.DBContract.DiscoverEntry;
import com.squareup.picasso.Picasso;

public class PopularMoviesList extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] DiscoverEntryColsArray = {
            DiscoverEntry._ID,
            DiscoverEntry.MOVIE_ID,
            DiscoverEntry.MOVIE_TITLE,
            DiscoverEntry.MOVIE_OVERVIEW,
            DiscoverEntry.MOVIE_POSTER_PATH
    };

    SimpleCursorAdapter moviesAdapter;

    private static final int MoviesLoader = 0;

    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_TITLE = "movie_title";
    public static final String MOVIE_OVERVIEW = "movie_overview";

    public PopularMoviesList() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MoviesLoader, null,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_popular_movies_list, container, false);

        GridView moviesGridList = (GridView) view.findViewById(R.id.moviesGridList);

        moviesAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.main_content_grid_item,
                null,
                new String[] {DiscoverEntry.MOVIE_POSTER_PATH},
                new int[] {R.id.movie_poster},
                0
        );

        moviesAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185" + cursor.getString(cursor.getColumnIndex(DiscoverEntry.MOVIE_POSTER_PATH))).into((ImageView) view);
                return true;
            }
        });

        moviesGridList.setAdapter(moviesAdapter);

        moviesGridList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = moviesAdapter.getCursor();
                cursor.moveToPosition(i);

                String movie_id = cursor.getString(cursor.getColumnIndex(DiscoverEntry.MOVIE_ID));
                String movie_title = cursor.getString(cursor.getColumnIndex(DiscoverEntry.MOVIE_TITLE));

                Snackbar.make(view, "Loading Movie " + movie_id + " : " + movie_title, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(getActivity(), DetailsScreen.class);
                intent.putExtra(MOVIE_ID, movie_id);
                intent.putExtra(MOVIE_TITLE, movie_title);
                intent.putExtra(MOVIE_OVERVIEW, cursor.getString(cursor.getColumnIndex(DiscoverEntry.MOVIE_OVERVIEW)));

                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                DiscoverEntry.Content_Uri,
                DiscoverEntryColsArray,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        moviesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviesAdapter.swapCursor(null);
    }
}
