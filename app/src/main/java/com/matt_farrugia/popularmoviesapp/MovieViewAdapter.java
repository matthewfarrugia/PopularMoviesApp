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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    public void sortData(String orderBy) throws JSONException {
        if (mDataHolder != null) {
            List<JSONObject> JSONList = new ArrayList<>();
            for (int i = 0; i < mDataHolder.length(); i++) {
                JSONList.add(mDataHolder.getJSONObject(i));
            }
            class JSONComparator implements Comparator<JSONObject> {
                private String mOrderBy;

                private JSONComparator(String orderBy) {
                    mOrderBy = orderBy;
                }

                public int compare(JSONObject a, JSONObject b) {
                    try {
                        int valA = a.getInt(mOrderBy);
                        int valB = b.getInt(mOrderBy);
                        return valB - valA;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            }
            Collections.sort(JSONList, new JSONComparator(orderBy));
            JSONArray SortedJSON = new JSONArray();
            for (int i = 0; i < JSONList.size(); i++) {
                SortedJSON.put(i, JSONList.get(i));
            }
            mDataHolder = SortedJSON;
            notifyDataSetChanged();
        }
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
