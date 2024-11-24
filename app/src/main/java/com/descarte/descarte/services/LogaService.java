package com.descarte.descarte.services;

import android.content.Context;

import com.descarte.descarte.R;
import com.descarte.descarte.entitties.Coleta;
import com.descarte.descarte.entitties.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LogaService {

    double lat;
    double lng;
    int distance = 100;

    public LogaService(double Latitude, double Longitute){
        this.lat = Latitude;
        this.lng = Longitute;
    }

    public LogaService(double Latitude, double Longitute, int distance){
        this(Latitude,Longitute);
        this.distance = distance;
    }

    public String ApiUrlBuild(Context context){
        return context.getString(R.string.LogaUrl) + "?distance=" + distance + "&lat="+lat+"&lng="+lng+"&agruparLogradourosMesmaRua=true";
    }

    public void GetDataAsync(Context context,OnDataReceivedListener listener) {
        Connection conn = new Connection(ApiUrlBuild(context));
        conn.getDataAsync().thenAccept(resultString -> {
            List<Data> results = new ArrayList<Data>();
            try {
                JSONObject fullResult = new JSONObject(resultString);
                if (fullResult.getBoolean("found")) {
                    JSONArray Logradouros = fullResult.getJSONObject("result").getJSONArray("Logradouros");

                    for (int i = 0; i < Logradouros.length(); i++) {
                        JSONObject result = Logradouros.getJSONObject(i);
                        Data data = new Data();

                        String tipo = result.optString("Tipo", "-");
                        String titulo = result.optString("Titulo", "-");
                        String preposicao = result.optString("Preposicao", "-");
                        String logradouro = result.optString("Logradouro", "-");

                        // Caso queira tratar de forma mais rigorosa, para não aceitar valores vazios:
                        tipo = tipo.isEmpty() ? "-" : tipo;
                        titulo = titulo.isEmpty() ? "-" : titulo;
                        preposicao = preposicao.isEmpty() ? "-" : preposicao;
                        logradouro = logradouro.isEmpty() ? "-" : logradouro;

                        data.setEndereco(tipo, titulo, preposicao, logradouro);
                        data.Subprefeitura = result.getString("Subprefeitura");
                        data.Cep = result.getString("Cep");


                        JSONObject domiciliar = result.getJSONObject("Domiciliar");
                        if (domiciliar != null) {
                            data.Domiciliar = new Coleta(
                                    domiciliar.optBoolean("HasSeg", false),
                                    domiciliar.optBoolean("HasTer", false),
                                    domiciliar.optBoolean("HasQua", false),
                                    domiciliar.optBoolean("HasQui", false),
                                    domiciliar.optBoolean("HasSex", false),
                                    domiciliar.optBoolean("HasSab", false),
                                    domiciliar.optBoolean("HasDom", false)
                            );
                            data.Domiciliar.Periodo = domiciliar.getString("Periodo");
                        } else {
                            data.Domiciliar = new Coleta(true);
                        }

                        JSONObject seletiva = result.getJSONObject("Seletiva");
                        if (seletiva != null){
                            data.Seletiva = new Coleta(
                                    seletiva.optBoolean("HasSeg", false),
                                    seletiva.optBoolean("HasTer",false),
                                    seletiva.optBoolean("HasQua", false),
                                    seletiva.optBoolean("HasQui", false),
                                    seletiva.optBoolean("HasSex", false),
                                    seletiva.optBoolean("HasSab",false),
                                    seletiva.optBoolean("HasDom",false)
                            );
                            data.Seletiva.Periodo = seletiva.getString("Periodo");
                        }
                        else {
                            data.Seletiva = new Coleta(true);
                        }
                        results.add(data);
                        System.out.println(data);
                    }
                } else {
                    System.out.println("Nada encontrado.");
                }
            } catch (JSONException e) {
                //return null;
            }

            listener.onDataReceived(results);
        });
    }

    public interface OnDataReceivedListener {
        void onDataReceived(List<Data> results);
    }
}
