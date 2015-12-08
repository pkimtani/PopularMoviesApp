package com.pkimtani.android.popularmoviesapp.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DBContract {

    public static final String Content_Auth = "com.pkimtani.android.popularmoviesapp.app";

    public static final Uri Base_Content_Uri = Uri.parse("content://"+Content_Auth);

    public static final String Path_DISCOVER_TB = "discover";
    public static final String Path_MOVIE_TB = "movie";

    public static class DiscoverEntry implements BaseColumns{

        public static final Uri Content_Uri = Base_Content_Uri.buildUpon().appendPath(Path_DISCOVER_TB).build();

        public static final String Content_Type_Dir = "vnd.android.cursor.dir/"+Content_Auth+"/"+Path_DISCOVER_TB;
        public static final String Content_Type_Item = "vnd.android.cursor.item/"+Content_Auth+"/"+Path_DISCOVER_TB;

        public static final String TB_NAME = "discover";
        public static final String MOVIE_ID = "movie_id";
        public static final String MOVIE_TITLE = "movie_title";
        public static final String MOVIE_OVERVIEW = "movie_overview";
        public static final String MOVIE_POSTER_PATH = "movie_poster_path";

        public static Uri buildMovieUriFromId(long _id)
        {
            return ContentUris.withAppendedId(Content_Uri, _id);
        }

        public static String getMovieIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

    }

    public static class MovieEntry implements BaseColumns {

        public static final Uri Content_Uri = Base_Content_Uri.buildUpon().appendPath(Path_MOVIE_TB).build();

        public static final String Content_Type_Dir = "vnd.android.cursor.dir/"+Content_Auth+"/"+Path_MOVIE_TB;
        public static final String Content_Type_Item = "vnd.android.cursor.item/"+Content_Auth+"/"+Path_MOVIE_TB;

        public static final String TB_NAME = "movie";
        public static final String MOVIE_ID = "movie_id";
        public static final String MOVIE_RELEASE_DATE = "movie_release_date";
        public static final String MOVIE_VOTE_AVG = "movie_vote_avg";
        public static final String MOVIE_IMG_PATH = "movie_img_path";

        public static Uri buildMovieUriFromId(long _id)
        {
            return ContentUris.withAppendedId(Content_Uri, _id);
        }

        public static String getMovieIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

    }

}
