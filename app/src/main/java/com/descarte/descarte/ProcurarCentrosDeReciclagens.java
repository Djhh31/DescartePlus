package com.descarte.descarte;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.descarte.descarte.databinding.ActivityProcurarCentrosDeReciclagensBinding;
import com.descarte.descarte.entitties.CentroDeColeta;
import com.descarte.descarte.services.GoogleMapsService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class ProcurarCentrosDeReciclagens extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ActivityProcurarCentrosDeReciclagensBinding binding;
    private Marker selectedMarker = null;
    private LatLng UserLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProcurarCentrosDeReciclagensBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializa o FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.DarkGreen));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        ImageButton backbutton = findViewById(R.id.Backbutton);
        backbutton.setOnClickListener(v -> {
            Intent intent = new Intent(ProcurarCentrosDeReciclagens.this, MainActivity.class);
            startActivity(intent);
        });

    }

    public void onMapReady(@NonNull GoogleMap map) {

        googleMap = map;

        // Configura o mapa
        if (ActivityCompat.checkSelfPermission(ProcurarCentrosDeReciclagens.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            // Solicitar permissão, se necessário
            ActivityCompat.requestPermissions(ProcurarCentrosDeReciclagens.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // Desativa o botão padrão
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        // Configura botão personalizado
        ImageButton myLocationButton = findViewById(R.id.FindMe);
        myLocationButton.setOnClickListener(v -> getDeviceLocation());

        ImageButton toMaps = findViewById(R.id.FindOnMaps);
        toMaps.setVisibility(View.GONE);
        googleMap.setOnMarkerClickListener(marker -> {
            selectedMarker = marker;
            toMaps.setVisibility(View.VISIBLE);
            return false;
        });

        googleMap.setOnMapClickListener(latLng -> {
            selectedMarker = null;
            toMaps.setVisibility(View.GONE);
        });


        toMaps.setOnClickListener(v -> {
            if (selectedMarker == null){
                Toast.makeText(ProcurarCentrosDeReciclagens.this, "Nenhum marker encontrado!",Toast.LENGTH_SHORT).show();
                toMaps.setVisibility(View.GONE);
            } else {
                CentroDeColeta data = (CentroDeColeta) selectedMarker.getTag();
                assert data != null;
                openPlaceInGoogleMapsById(data);
            }
        });

        getDeviceLocation();

        if (UserLocation!= null){
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UserLocation, 200));
        } else {
            // Move a câmera para São Paulo
            LatLng saoPaulo = new LatLng(-23.550520, -46.633308);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(saoPaulo, 100));
        }
    };

    private List<CentroDeColeta> cachedResults;

    private void openPlaceInGoogleMapsById(CentroDeColeta data) {

        StringBuilder sb = new StringBuilder();
        sb.append("https://www.google.com/maps/search/?api=1&query=");
        sb.append(data.lat).append(",").append(data.lng);
        sb.append("&query_place_id=").append(data.id);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sb.toString()));
        intent.setPackage("com.google.android.apps.maps"); // Garante que o Google Maps será usado

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent); // Abre o Google Maps
        } else {
            Toast.makeText(this, "Google Maps não está instalado", Toast.LENGTH_SHORT).show();
        }
    }

    private int Radius = 2000;
    private void fetchAndDisplayMarkers(LatLng location) {
        googleMap.clear();
        if (cachedResults != null) {
            displayMarkers(cachedResults);
        } else {
            GoogleMapsService gms = new GoogleMapsService(
                    "reciclagem perto de mim",
                    location.latitude,
                    location.longitude,
                    Radius
            );


            gms.getAllDataAsync(ProcurarCentrosDeReciclagens.this, new GoogleMapsService.OnDataReceivedListener() {
                @Override
                public void onDataReceived(List<CentroDeColeta> results) {
                    cachedResults = results; // Armazena em cache
                    runOnUiThread(() -> {
                        displayMarkers(results);
                    });
                }
            });
        }
    }

    private void displayMarkers(List<CentroDeColeta> results) {
        if (results != null && !results.isEmpty()) {
            for (CentroDeColeta data : results) {
                LatLng markerLocation = new LatLng(data.lat, data.lng);
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(markerLocation)
                        .title(data.name)
                        );
                assert marker != null;
                marker.setTag(data);
            }
        }
    }

    // Obtém a localização atual do dispositivo
    private void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            // Movimenta a câmera para a localização atual
                            UserLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(UserLocation, 15));

                            fetchAndDisplayMarkers(UserLocation);
                        }
                    });
        } else {
            // Solicita permissão de localização, se ainda não concedida
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getDeviceLocation();
        }
    }
}

