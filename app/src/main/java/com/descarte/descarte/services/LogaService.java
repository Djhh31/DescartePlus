package com.descarte.descarte.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.descarte.descarte.entitties.Data;

public class LogaService extends UniversalService {

    private final String ApiUrl = "https://webservices.loga.com.br/sgo/eresiduos/BuscaPorLatLng?distance=100&lat=-23.4913981&lng=-46.5896529";

    public LogaService(double Latitude, double Longitute){
        super(Latitude, Longitute);
    }

    public LogaService(double Latitude, double Longitute, int distance){
        super( Latitude, Longitute, distance);
    }

    @Override
    public String ApiUrlBuild(){
        return ApiUrl + "distance=" + distance + "&lat="+lat+"&lng="+lng+"&agruparLogradourosMesmaRua=true";
    }

    @Override
    public List<Data> GetData() {
        Connection conn = new Connection(ApiUrlBuild());
        AtomicReference<List<Data>> results = new AtomicReference<>(new ArrayList<>());

        conn.getDataAsync().thenAccept(resultString -> {
            try {
                JSONObject fullResult = new JSONObject(resultString);
                if (fullResult.getBoolean("found")){
                    JSONArray Logradouros = fullResult.getJSONObject("result").getJSONArray("Logradouros");

                    for (int i = 0; i < Logradouros.length(); i++) {

                        JSONObject result = Logradouros.getJSONObject(i);
                        Data data = new Data();

                        data.Endereco(result.getString("Tipo"),result.getString("Logradouro"));
                        data.Subprefeitura = result.getString("Subprefeitura");
                        data.Cep = result.getString("Cep");

                        JSONObject domiciliar = result.getJSONObject("Domiciliar");
                        data.Domiciliar.Seg = domiciliar.getBoolean("HasSeg");
                        data.Domiciliar.Ter = domiciliar.getBoolean("HasTer");
                        data.Domiciliar.Qua = domiciliar.getBoolean("HasQua");
                        data.Domiciliar.Qui = domiciliar.getBoolean("HasQui");
                        data.Domiciliar.Sex = domiciliar.getBoolean("HasSex");
                        data.Domiciliar.Sab = domiciliar.getBoolean("HasSab");
                        data.Domiciliar.Dom = domiciliar.getBoolean("HasDom");
                        data.Domiciliar.Periodo(domiciliar.getString("Periodo"));

                        JSONObject seletiva = result.getJSONObject("Seletiva");
                        data.Seletiva.Seg = seletiva.getBoolean("HasSeg");
                        data.Seletiva.Ter = seletiva.getBoolean("HasTer");
                        data.Seletiva.Qua = seletiva.getBoolean("HasQua");
                        data.Seletiva.Qui = seletiva.getBoolean("HasQui");
                        data.Seletiva.Sex = seletiva.getBoolean("HasSex");
                        data.Seletiva.Sab = seletiva.getBoolean("HasSab");
                        data.Seletiva.Dom = seletiva.getBoolean("HasDom");
                        data.Seletiva.Periodo(seletiva.getString("Periodo"));

                        results.get().add(data);
                    }
                } else {
                    System.out.println("Nada encontrado.");
                    results.set(null);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        return results.get();
    }
}
