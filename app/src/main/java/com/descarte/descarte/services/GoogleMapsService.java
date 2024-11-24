package com.descarte.descarte.services;

import android.content.Context;

import com.descarte.descarte.R;
import com.descarte.descarte.entitties.CentroDeColeta;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GoogleMapsService {

    private final String search;
    private final double latitude;
    private final double longitude;
    private final int radius;

    public GoogleMapsService(String search, double latitude, double longitude, int radius) {
        this.search = search;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    // Usando StringBuilder para construir a URL
    private String urlBuilder(Context context, String nextPageToken) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json");

        if (nextPageToken != null) {
            url.append("?pagetoken=").append(nextPageToken);
        } else {
            url.append("?query=").append(search)
                    .append("&location=").append(latitude).append(",").append(longitude)
                    .append("&radius=").append(radius);
        }

        url.append("&key=").append(context.getString(R.string.GoogleKey));
        return url.toString();
    }

    public void getAllDataAsync(final Context context, final OnDataReceivedListener listener) {
        List<CentroDeColeta> results = new ArrayList<>();
        fetchData(context, null, results ,listener);
    }

    private void fetchData(final Context context, String nextPageToken, List<CentroDeColeta> results, final OnDataReceivedListener listener) {
        String url = urlBuilder(context, nextPageToken);

        new Connection(url).getDataAsync().thenAccept(resultString -> {
            results.addAll(parseResults(resultString));

            try {
                JSONObject jsonResponse = new JSONObject(resultString);
                if (jsonResponse.has("next_page_token")) {
                    String nextToken = jsonResponse.getString("next_page_token");
                    fetchData(context, nextToken, results, listener);
                } else {
                    listener.onDataReceived(results);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private List<CentroDeColeta> parseResults(String resultString) {
        List<CentroDeColeta> centroDeColetaList = new ArrayList<>();
        try {
            JSONObject jsonResponse = new JSONObject(resultString);
            JSONArray resultsArray = jsonResponse.getJSONArray("results");

            // Processa os resultados e os adiciona à lista
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject result = resultsArray.getJSONObject(i);
                String id = result.optString("place_id");
                String name = result.optString("name");
                String address = result.optString("formatted_address");
                double lat = result.getJSONObject("geometry").getJSONObject("location").optDouble("lat");
                double lng = result.getJSONObject("geometry").getJSONObject("location").optDouble("lng");

                CentroDeColeta centro = new CentroDeColeta(id,name, address, lat, lng);
                centroDeColetaList.add(centro);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return centroDeColetaList;
    }

    public interface OnDataReceivedListener {
        void onDataReceived(List<CentroDeColeta> results);
    }
}