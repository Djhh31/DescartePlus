package com.descarte.descarte;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.descarte.descarte.entitties.Data;
import com.descarte.descarte.services.LogaService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActivityPesquisarColeta extends AppCompatActivity {

    private ProgressBar Loading;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_pesquisar_coleta);

        // Set Loading Progress Bar
        Loading = findViewById(R.id.loading);
        Window window = getWindow();

        // Set Notification bar color
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.DarkGreen));

        //Search Box Function
        TextView SearchBox = findViewById(R.id.SearchTextBox);
        ImageButton ClearButton = findViewById(R.id.ClearButton);
        SearchBox.addTextChangedListener(new TextWatcher() {
           @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
              //Before Text Change
          }

          @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
              clearMainLayout();
               if (charSequence.length() > 0){
                   ClearButton.setVisibility(View.VISIBLE);
                   obterCoordenadasDeEndereco(SearchBox.getText().toString());
               }else{
                   ClearButton.setVisibility(View.GONE);
               };
           }

         @Override
           public void afterTextChanged(Editable editable) {
               //After Text Change
           }
       });

        // Botão de limpar o SearchBox
        ClearButton.setOnClickListener(v ->{
            SearchBox.setText("");
            clearMainLayout();
        });

        // Botão de retorno ao Home
        ImageButton backbutton = findViewById(R.id.Backbutton);
        backbutton.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityPesquisarColeta.this, MainActivity.class);
            startActivity(intent);
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Button UsarLocaluizacaoAtual = findViewById(R.id.UsarLocaluizacaoAtual);
        UsarLocaluizacaoAtual.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
            }else{
                runWithLastLocation();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    // Clear Content Layout
    private void clearMainLayout(){

        message("");

        runOnUiThread(() -> {
            LinearLayout parentLayout = findViewById(R.id.content);
            if (parentLayout.getChildCount() > 0) {
                for (int i = parentLayout.getChildCount() - 1; i >= 0; i--) {
                    View view = parentLayout.getChildAt(i);
                    view.setOnClickListener(null); // Remove o listener
                }
                parentLayout.removeAllViews();
            }
        });
    }

    //conexão e display com a API apartir da Localização do telefone
    private void runWithLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Verificando se a localização não é nula
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            TryConnection(latitude,longitude);
                            clearMainLayout();
                        } else {
                            System.out.println("Não foi possível obter a localização.");
                            message("Não foi possível obter a localização.");
                        }
                    }
                });
    }

    //conexão e display com a API apartir do endereço em String
    public void obterCoordenadasDeEndereco(String endereco) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        endereco = enderecoRefine(endereco);

        try {
            // Solicita a lista de endereços correspondentes
            List<Address> enderecoList = geocoder.getFromLocationName(endereco, 1); // O '1' significa que queremos no máximo 1 resultado

            if (enderecoList != null && !enderecoList.isEmpty()) {
                Address address = enderecoList.get(0); // Pega o primeiro endereço retornado
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();

                // Log das coordenadas
                TryConnection(latitude,longitude);
            } else {
                message("Endereço não encontrado");
                Log.d("Localização", "Endereço não encontrado");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Erro", "Erro ao obter as coordenadas");
            message("Erro ao obter as coordenadas! tente novamente. Se o erro persistir nos contate");
        }
    }

    // Limpesa do endereço
    private String enderecoRefine(String endereco) {
        // Mapeamento das abreviações
        Map<String, String> abreviacoes = new HashMap<>();

        // Abreviações de logradouro
        abreviacoes.put("r ", "rua ");
        abreviacoes.put("r ", "rua ");
        abreviacoes.put("av ", "avenida ");
        abreviacoes.put("tv ", "travessa ");
        abreviacoes.put("pc ", "praça ");
        abreviacoes.put("al ", "alameda ");
        abreviacoes.put("lgo ", "largo ");
        abreviacoes.put("estr ", "estrada ");
        abreviacoes.put("rod ", "rodovia ");
        abreviacoes.put("bl ", "bloco ");
        abreviacoes.put("qd ", "quadra ");
        abreviacoes.put("cj ", "conjunto ");
        abreviacoes.put("bs ", "beco ");
        abreviacoes.put("v ", "viação ");
        abreviacoes.put("c ", "caminho ");

        // Abrev de titulos de logradouro
        abreviacoes.put("dr ", "doutor ");
        abreviacoes.put("prof ", "professor ");
        abreviacoes.put("com ", "comendador ");
        abreviacoes.put("eng ", "engenheiro ");
        abreviacoes.put("cel ", "coronel ");
        abreviacoes.put("v ", "visconde ");
        abreviacoes.put("sra ", "senhora ");
        abreviacoes.put("m ", "marechal ");
        abreviacoes.put("tte ", "tenente ");

        // Converter para minúsculas
        endereco = endereco.toLowerCase();

        // Adicionar espaço no final
        endereco = endereco + " ";
        endereco = endereco.replaceAll("[^a-zA-Z0-9\\s]", "");

        endereco = endereco.replace(".", "");
        // Iterar sobre o mapa e substituir as abreviações
        for (Map.Entry<String, String> entry : abreviacoes.entrySet()) {
            endereco = endereco.replace(entry.getKey(), entry.getValue());
        }
        return endereco.trim();
    }

    public void TryConnection(double lat, double lgn){
        try {
            runOnUiThread(() -> Loading.setVisibility(View.VISIBLE));
            LogaService LogaS = new LogaService(lat,lgn);

            LogaS.GetDataAsync(results -> {
                runOnUiThread(() -> {
                    if (results != null && !results.isEmpty()) {
                        LinearLayout parentLayout = findViewById(R.id.content);
                        for (Data data : results) {
                            CardView newCard = CreateViewCard(data,parentLayout,this);
                            parentLayout.addView(newCard);
                        }
                        Loading.setVisibility(View.GONE);
                    } else {
                        System.out.println("Lista está vazia ou nula.");
                        message("Nenhum resultado encontrado! ");
                        Loading.setVisibility(View.GONE);
                    }
                });
            });

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            message("Algo deu errado..." + ex.getMessage());
        }
    }

    public void message(String string){
        TextView message = findViewById(R.id.message);
        runOnUiThread(() -> {message.setText(string);
            });
    }

    public CardView CreateViewCard(Data data, ViewGroup parentLayout, Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        CardView clonedCardView = (CardView) inflater.inflate(R.layout.cardview, parentLayout, false);

        TextView end = clonedCardView.findViewById(R.id.endereco);
        end.setText(data.Endereco);
        TextView cep = clonedCardView.findViewById(R.id.cep);
        cep.setText(data.Cep);
        TextView sub = clonedCardView.findViewById(R.id.subPrefeitura);
        sub.setText(data.Subprefeitura);

        TextView periodoCommum = clonedCardView.findViewById(R.id.comum_periodo);
        String periodoComumString =  data.Domiciliar.Periodo.toString();
        periodoCommum.setText(periodoComumString);

        TextView periodoReciclavel = clonedCardView.findViewById(R.id.reciclavel_periodo);
        String periodoReciclavelString = data.Seletiva.Periodo.toString();
        periodoReciclavel.setText(periodoReciclavelString);

        TextView semanaComum =  clonedCardView.findViewById(R.id.comum_semana);
        semanaComum.setText(data.Domiciliar.toString());

        TextView semanaReciclavel =  clonedCardView.findViewById(R.id.reciclavel_semana);
        semanaReciclavel.setText(data.Seletiva.toString());

        return clonedCardView;
    }

}