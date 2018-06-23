package com.matt_farrugia.popularmoviesapp;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

class NetworkUtils {
    private static final String popularBaseUrl = "api.themoviedb.org/3/movie/popular";
    private static final String imageBaseUrl = "image.tmdb.org/t/p/";
    private static final String apiKey = "7fa1c58635370522f5a3cc9b5a7e3e68";

    private static Uri buildUri(){
        String scheme = "https";
        String query = "api_key";
        return new Uri.Builder().scheme(scheme).path(popularBaseUrl).appendQueryParameter(query,apiKey).build();
    }

    public static String buildImageUrl(String imagePath){
        String scheme = "https";
        String imageSize = "w185";
        String imageUrl = new Uri.Builder().scheme(scheme).path(imageBaseUrl + imageSize + imagePath).toString();
        Log.d("Fetching image from", imageUrl);
        return imageUrl;
    }

    public static String fetchPopularMovies(){
        try {
            URL url = new URL(buildUri().toString());
            Log.d("Fetching from Url", url.toString());
            return getResponseFromHttpUrl(url);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
