package com.pkimtani.android.popularmoviesapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class HomeScreen extends AppCompatActivity {

    public static final String LOG_TAG = "PopularMoviesApp";

    public static final String SORT_KEY = "sort";

    public static SharedPreferences sortPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        sortPreference = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = sortPreference.edit();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder sortOrderDialog = new AlertDialog.Builder(HomeScreen.this);
                View sortView = getLayoutInflater().inflate(R.layout.sort_order_select, null);
                sortOrderDialog.setView(sortView);
                sortOrderDialog.show();

                Spinner sortSpinner = (Spinner) sortView.findViewById(R.id.sort_spinner);

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(HomeScreen.this,
                        R.array.sort_order_list, android.R.layout.simple_spinner_item);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                sortSpinner.setAdapter(adapter);

                sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        //update shared preference
                        editor.putString(SORT_KEY, (String) adapterView.getItemAtPosition(i));
                        editor.apply();
                        startService(new Intent(HomeScreen.this, FetchMovieService.class));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        //do nothing
                        editor.putString(SORT_KEY, "popularity");
                        editor.apply();
                        startService(new Intent(HomeScreen.this, FetchMovieService.class));
                    }
                });

            }


        });

        startService(new Intent(HomeScreen.this, FetchMovieService.class));
    }
}
