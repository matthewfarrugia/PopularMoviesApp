package com.matt_farrugia.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements MovieViewAdapter.MovieOnClickHandler  {

    private RecyclerView mRecyclerViewMainActivity;
    private TextView mErrorMessageDisplay;
    private ProgressBar mProgressBar;

    private MovieViewAdapter mMovieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerViewMainActivity = findViewById(R.id.rv_main_activity);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message);
        mProgressBar = findViewById(R.id.pb_progress_loader);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerViewMainActivity.setLayoutManager(gridLayoutManager);
        mRecyclerViewMainActivity.setHasFixedSize(true);

        mMovieAdapter = new MovieViewAdapter(this);
        mRecyclerViewMainActivity.setAdapter(mMovieAdapter);

        loadMovieData(Constants.popularBaseUrl);
    }

    private void showProgressBar(){
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.GONE);
        mRecyclerViewMainActivity.setVisibility(View.GONE);
    }

    private void showMovieData(){
        mProgressBar.setVisibility(View.GONE);
        mErrorMessageDisplay.setVisibility(View.GONE);
        mRecyclerViewMainActivity.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(){
        mProgressBar.setVisibility(View.GONE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mRecyclerViewMainActivity.setVisibility(View.GONE);
    }

    private void loadMovieData(String endpoint){
        new fetchFromSource(this, endpoint).execute();
    }

    @Override
    public void onClick(JSONObject movieDetails) {
        Context context = this;
        Intent intent = new Intent(context, DetailedActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, movieDetails.toString());
        startActivity(intent);
    }

    static class fetchFromSource extends AsyncTask<Void, Void, String> {

        private final WeakReference<MainActivity> activityReference;
        private final String mEndpoint;

        // only retain a weak reference to the activity
        fetchFromSource(MainActivity context, String endpoint) {
            activityReference = new WeakReference<>(context);
            mEndpoint = endpoint;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                return NetworkUtils.fetchPopularMovies(mEndpoint);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String movieData) {
            MainActivity activity = activityReference.get();
            if (movieData != null) {
                activity.mMovieAdapter.setData(movieData);
                activity.showMovieData();
            } else {
                activity.showErrorMessage();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.movie_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort_by_rating) {
            loadMovieData(Constants.topRatedBaseUrl);
            showProgressBar();
            return true;
        } else if (id == R.id.sort_by_popularity) {
            loadMovieData(Constants.popularBaseUrl);
            showProgressBar();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
