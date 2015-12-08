package com.pkimtani.android.popularmoviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pkimtani.android.popularmoviesapp.HomeScreen;
import com.pkimtani.android.popularmoviesapp.data.DBContract.DiscoverEntry;
import com.pkimtani.android.popularmoviesapp.data.DBContract.MovieEntry;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBName = "popular_movies";
    public static final int DBVer = 1;

    public DBHelper(Context context) {
        super(context, DBName, null, DBVer);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String create_TB_Query_Discover = "CREATE TABLE IF NOT EXISTS " + DiscoverEntry.TB_NAME + " ( " +
                DiscoverEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DiscoverEntry.MOVIE_ID + " TEXT NOT NULL, " +
                DiscoverEntry.MOVIE_TITLE + " TEXT NOT NULL, " +
                DiscoverEntry.MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                DiscoverEntry.MOVIE_POSTER_PATH + " TEXT NOT NULL ); " +
                " UNIQUE (  " + DiscoverEntry.MOVIE_ID + " ) ON CONFLICT " + " REPLACE );";

        final String create_TB_Query_Movie = "CREATE TABLE IF NOT EXISTS " + MovieEntry.TB_NAME + " ( " +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.MOVIE_ID + " TEXT NOT NULL, " +
                MovieEntry.MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.MOVIE_IMG_PATH + " TEXT NOT NULL, " +
                MovieEntry.MOVIE_VOTE_AVG + " TEXT NOT NULL ); " +
                " UNIQUE (  " + MovieEntry.MOVIE_ID + " ) ON CONFLICT " + " REPLACE );";

        sqLiteDatabase.execSQL(create_TB_Query_Discover);
        sqLiteDatabase.execSQL(create_TB_Query_Movie);
        Log.i(HomeScreen.LOG_TAG, "DBHelper | tables created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DiscoverEntry.TB_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TB_NAME);
        onCreate(sqLiteDatabase);
    }
}
