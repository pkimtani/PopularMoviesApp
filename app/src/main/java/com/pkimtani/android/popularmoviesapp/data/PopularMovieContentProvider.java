package com.pkimtani.android.popularmoviesapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

public class PopularMovieContentProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private DBHelper mOpenHelper;

    static final int DISCOVER = 100;
    static final int DISCOVER_WITH_MOVIE_ID = 101;
    static final int MOVIE = 300;
    static final int MOVIE_WITH_MOVIE_ID = 301;

    private static final SQLiteQueryBuilder sMovieByDiscoverJoinQuery;

    static{
        sMovieByDiscoverJoinQuery = new SQLiteQueryBuilder();

        //This is an inner join
        sMovieByDiscoverJoinQuery.setTables(
                DBContract.MovieEntry.TB_NAME + " INNER JOIN " +
                        DBContract.DiscoverEntry.TB_NAME +
                        " ON " + DBContract.MovieEntry.TB_NAME +
                        "." + DBContract.MovieEntry.MOVIE_ID +
                        " = " + DBContract.DiscoverEntry.TB_NAME +
                        "." + DBContract.DiscoverEntry._ID);
    }

    //location.location_setting = ?
    private static final String sMovieIDSelection =
            DBContract.DiscoverEntry.TB_NAME +
                    "." + DBContract.DiscoverEntry.MOVIE_ID + " = ? ";

    private Cursor getMovieByID(Uri uri, String[] projection, String sortOrder) {
        String movieId = DBContract.DiscoverEntry.getMovieIdFromUri(uri);

        String[] selectionArgs = new String[]{movieId};
        String selection  =  sMovieIDSelection;

        return sMovieByDiscoverJoinQuery.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DBContract.Content_Auth;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, DBContract.Path_DISCOVER_TB, DISCOVER);
        matcher.addURI(authority, DBContract.Path_DISCOVER_TB + "/*", DISCOVER_WITH_MOVIE_ID);

        matcher.addURI(authority, DBContract.Path_MOVIE_TB, MOVIE);
        matcher.addURI(authority, DBContract.Path_MOVIE_TB + "/*", MOVIE_WITH_MOVIE_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "weather/*"
            case DISCOVER_WITH_MOVIE_ID: {
                retCursor = getMovieByID(uri, projection, sortOrder);
                break;
            }
            // "weather"
            case DISCOVER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DBContract.DiscoverEntry.TB_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "location"
            case MOVIE_WITH_MOVIE_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DBContract.MovieEntry.TB_NAME,
                        projection,
                        DBContract.MovieEntry.TB_NAME + "." + DBContract.MovieEntry.MOVIE_ID + " = ? ",
                        new String[] {DBContract.MovieEntry.getMovieIdFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DBContract.MovieEntry.TB_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case MOVIE_WITH_MOVIE_ID:
                return DBContract.MovieEntry.Content_Type_Item;
            case MOVIE:
                return DBContract.MovieEntry.Content_Type_Dir;
            case DISCOVER_WITH_MOVIE_ID:
                return DBContract.DiscoverEntry.Content_Type_Item;
            case DISCOVER:
                return DBContract.DiscoverEntry.Content_Type_Dir;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case DISCOVER: {

                long _id = db.insert(DBContract.DiscoverEntry.TB_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DBContract.DiscoverEntry.buildMovieUriFromId(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MOVIE: {
                long _id = db.insert(DBContract.MovieEntry.TB_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DBContract.MovieEntry.buildMovieUriFromId(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case DISCOVER:
                rowsDeleted = db.delete(
                        DBContract.DiscoverEntry.TB_NAME, selection, selectionArgs);
                break;
            case MOVIE:
                rowsDeleted = db.delete(
                        DBContract.MovieEntry.TB_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case DISCOVER:
                rowsUpdated = db.update(DBContract.DiscoverEntry.TB_NAME, values, selection, selectionArgs);
                break;
            case MOVIE:
                rowsUpdated = db.update(DBContract.MovieEntry.TB_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
