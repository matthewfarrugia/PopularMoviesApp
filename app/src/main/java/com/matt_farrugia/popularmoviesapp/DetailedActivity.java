package com.matt_farrugia.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        ImageView mMoviePoster = findViewById(R.id.tv_movie_poster);
        TextView mMovieTitle =  findViewById(R.id.tv_movie_title);
        TextView mMovieSummary = findViewById(R.id.tv_summary);
        TextView mMovieReleaseDate = findViewById(R.id.tv_release_date);
        TextView mMovieUserRating = findViewById(R.id.tv_user_review);

        Intent intent = getIntent();
        if (intent.hasExtra(Intent.EXTRA_TEXT)){
            String movieData = intent.getStringExtra(Intent.EXTRA_TEXT);
            try {
                JSONObject movieDataJSON = new JSONObject(movieData);
                mMovieTitle.setText(movieDataJSON.getString("title"));
                mMovieSummary.setText(movieDataJSON.getString("overview"));
                mMovieUserRating.setText(movieDataJSON.getString("vote_average"));
                mMovieReleaseDate.setText(movieDataJSON.getString("release_date"));
                Picasso.with(mMoviePoster.getContext())
                        .load(NetworkUtils.buildImageUrl(movieDataJSON.getString("poster_path")))
                        .into(mMoviePoster);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Context context = this;
            Intent abortActivity = new Intent(context, MainActivity.class);
            startActivity(abortActivity);
        }
    }

}
