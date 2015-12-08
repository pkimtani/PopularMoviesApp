package com.pkimtani.android.popularmoviesapp;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.pkimtani.android.popularmoviesapp.data.DBContract.MovieEntry;
import com.pkimtani.android.popularmoviesapp.data.DBContract.DiscoverEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchMovieService extends Service{

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new FetchMovieAsync().execute();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class FetchMovieAsync extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            HttpURLConnection urlConnection;
            BufferedReader reader;

            final String BASE_URL = "http://api.themoviedb.org/3/discover/movie";

            InputStream inputStream;
            StringBuffer buffer;

            final String URL_PARAM_SORT = "sort_by";
            final String URL_PARAM_API = "api_key";
            final String URL_PARAM_APPEND_RESPONSE = "append_to_response";

            //sort order and append to response parameter is set default behaviour in this stage.
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(FetchMovieService.this);
            String sort = pref.getString(HomeScreen.SORT_KEY, "popularity");
            String api = "";//your api key here
            String append_response = "images";

            ContentValues values;

            String moviesJSONResponse, movie_id, movie_title, movie_overview, movie_poster, release_date, img_path, vote_avg;

            final String ID = "id";
            final String TITLE = "title";
            final String OVERVIEW = "overview";
            final String POSTER_PATH = "poster_path";
            final String RELEASE_DATE = "release_date";
            final String IMG_PATH = "backdrop_path";
            final String VOTE_AVG = "vote_average";


            try {
                Uri listMovieUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(URL_PARAM_SORT, sort)
                        .appendQueryParameter(URL_PARAM_API, api)
                        .appendQueryParameter(URL_PARAM_APPEND_RESPONSE, append_response)
                        .build();

                URL listMovieUrl = new URL(listMovieUri.toString());

                urlConnection = (HttpURLConnection) listMovieUrl.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                inputStream = urlConnection.getInputStream();
                buffer = new StringBuffer();

                if(inputStream == null) {
                    //do something
                    Log.i(HomeScreen.LOG_TAG, "Empty response");
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while ((line = reader.readLine()) != null)
                {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                moviesJSONResponse = buffer.toString();

                JSONObject moviesJSONObject = new JSONObject(moviesJSONResponse);

                JSONArray moviesJSONArray = moviesJSONObject.getJSONArray("results");

                for(int i=0; i<moviesJSONArray.length(); i++){
                    JSONObject movieJSONObject = moviesJSONArray.getJSONObject(i);
                    movie_id = movieJSONObject.getString(ID);
                    movie_title = movieJSONObject.getString(TITLE);
                    movie_overview = movieJSONObject.getString(OVERVIEW);
                    movie_poster = movieJSONObject.getString(POSTER_PATH);
                    release_date = movieJSONObject.getString(RELEASE_DATE);
                    img_path = movieJSONObject.getString(IMG_PATH);
                    vote_avg = movieJSONObject.getString(VOTE_AVG);

                    values = new ContentValues();
                    values.put(DiscoverEntry.MOVIE_ID, movie_id);
                    values.put(DiscoverEntry.MOVIE_TITLE, movie_title);
                    values.put(DiscoverEntry.MOVIE_OVERVIEW, movie_overview);
                    values.put(DiscoverEntry.MOVIE_POSTER_PATH, movie_poster);

                    FetchMovieService.this.getContentResolver().insert(DiscoverEntry.Content_Uri, values);

                    values = new ContentValues();
                    values.put(MovieEntry.MOVIE_ID, movie_id);
                    values.put(MovieEntry.MOVIE_RELEASE_DATE, release_date);
                    values.put(MovieEntry.MOVIE_IMG_PATH, img_path);
                    values.put(MovieEntry.MOVIE_VOTE_AVG, vote_avg);

                    FetchMovieService.this.getContentResolver().insert(MovieEntry.Content_Uri, values);

                }

                Log.i(HomeScreen.LOG_TAG, "All done!");


            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Log.i(HomeScreen.LOG_TAG, e.toString());
            }

            return null;
        }
    }
}
