package com.matt_farrugia.popularmoviesapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieViewAdapter extends Adapter<MovieViewAdapter.MovieDataViewHolder> {

    private JSONArray mDataHolder;

    private final MovieOnClickHandler mClickHandler;

    public interface MovieOnClickHandler {
        void onClick(JSONObject movieDetails);
    }

    MovieViewAdapter (MovieOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }

    public void setData(String movieJSONString) {
        final String results = "results";

        try {
            JSONObject jsonResult = new JSONObject(movieJSONString);
            mDataHolder = jsonResult.getJSONArray(results);
        } catch (JSONException e){
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context =  parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new MovieDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieDataViewHolder holder, int position) {
        try {
            JSONObject movieObject = mDataHolder.getJSONObject(position);

            holder.mMovieTitle.setText(movieObject.getString("title"));
            Picasso.with(holder.mMoviePoster.getContext())
                    .load(NetworkUtils.buildImageUrl(movieObject.getString("poster_path")))
                    .into(holder.mMoviePoster);
            holder.mMoviePoster.setContentDescription(movieObject.getString("title") + "poster");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (mDataHolder == null) return 0;
        return mDataHolder.length();
    }

    public class MovieDataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView mMovieTitle;
        private final ImageView mMoviePoster;

        MovieDataViewHolder(View itemView) {
            super(itemView);
            mMovieTitle = itemView.findViewById(R.id.tv_movie_title);
            mMoviePoster = itemView.findViewById(R.id.tv_movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            try {
                JSONObject movieData = mDataHolder.getJSONObject(position);
                mClickHandler.onClick(movieData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
