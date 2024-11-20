package com.descarte.descarte;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
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

public class ActivityPesquisarColeta extends AppCompatActivity {

    private ProgressBar Loading;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_pesquisar_coleta);

        Loading = findViewById(R.id.loading);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.DarkGreen));

        ImageButton backbutton = findViewById(R.id.Backbutton);
        backbutton.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityPesquisarColeta.this, MainActivity.class);
            startActivity(intent);
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Button UsarLocaluizacaoAtual = findViewById(R.id.UsarLocaluizacaoAtual);
        UsarLocaluizacaoAtual.setOnClickListener(v -> {
            Log.d("ActivityPesquisarColeta","Pressed");
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,

                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
            }else{
                Log.d("ActivityPesquisarColeta","Pressed1");
                runWithLastLocation();
                Log.d("ActivityPesquisarColeta","Pressed2");
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void runWithLastLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Verificando se a localização não é nula
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            TryConnection(latitude,longitude);
                        } else {
                            System.out.println("Não foi possível obter a localização.");
                        }
                    }
                });
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
                        Loading.setVisibility(View.GONE);
                    }
                });
            });

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
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