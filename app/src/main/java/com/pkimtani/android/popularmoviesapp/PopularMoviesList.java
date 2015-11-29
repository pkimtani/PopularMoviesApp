package com.pkimtani.android.popularmoviesapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class PopularMoviesList extends Fragment {

    private View view;
    private GridView moviesGridList;
    private SimpleAdapter moviesAdapter;

    public PopularMoviesList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_popular_movies_list, container, false);

        moviesGridList = (GridView) view.findViewById(R.id.moviesGridList);

        moviesAdapter = new SimpleAdapter(getActivity(), null, R.layout.main_content_grid_item, null, null);

        moviesGridList.setAdapter(moviesAdapter);

        return view;
    }

}
