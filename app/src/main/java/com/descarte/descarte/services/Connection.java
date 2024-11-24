package com.descarte.descarte.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class Connection {

    protected String urlString;

    protected Connection(String APIUrl){
        urlString = APIUrl;
    }

    public CompletableFuture<String> getDataAsync() {
        return CompletableFuture.supplyAsync(this::callApi);
    }

    private String callApi(){
        String dataString = "";
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            int status = urlConnection.getResponseCode();
            if (status == 200) {
                dataString = readerStream(urlConnection.getInputStream());
            } else {
                System.err.println("Falha na consulta. Código de status: " + status);
            }
        } catch (Exception ex) {
            System.err.println("Falha na consulta: " + ex.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return dataString;
    }

    private String readerStream(InputStream in) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
}
