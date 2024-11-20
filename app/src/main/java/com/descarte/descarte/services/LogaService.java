package com.descarte.descarte.services;

import com.descarte.descarte.entitties.Coleta;
import com.descarte.descarte.entitties.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LogaService extends UniversalService {

    private final String ApiUrl = "https://webservices.loga.com.br/sgo/eresiduos/BuscaPorLatLng?";

    public LogaService(double Latitude, double Longitute){
        super(Latitude, Longitute);
        this.distance = 100;
    }

    public LogaService(double Latitude, double Longitute, int distance){
        super( Latitude, Longitute, distance);
    }

    @Override
    public String ApiUrlBuild(){
        return ApiUrl + "distance=" + distance + "&lat="+lat+"&lng="+lng+"&agruparLogradourosMesmaRua=true";
    }

    @Override
    public void GetDataAsync(OnDataReceivedListener listener) {
        Connection conn = new Connection(ApiUrlBuild());
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
                            data.Domiciliar.Periodo(domiciliar.getString("Periodo"));
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
                            data.Seletiva.Periodo(seletiva.getString("Periodo"));
                        }
                        else {
                            data.Domiciliar = new Coleta(true);
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

}
