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
        return CompletableFuture.supplyAsync(this::CallApi);
    }

    private String CallApi(){

        String dataString = "";
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("Get");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            int status = urlConnection.getResponseCode();
            System.out.println("API Request URL: " + urlString);
            System.out.println("API Request Status: " + status);

            if (status == 200) { // Código de sucesso HTTP
                dataString = readerStream(urlConnection.getInputStream());
            } else {
                System.out.println("Falha na consulta. Código de status: " + status);
            }
        } catch (Exception ex){
            System.out.println("Falha na consulta. " + ex.getMessage());
        } finally {
            try {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            catch (Exception ex){
                System.out.println("Falha na consulta. " + ex.getMessage());
            }
        }
        return dataString;
    }

    private String readerStream(InputStream in){

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
