package com.descarte.descarte;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        ProgressBar loading = findViewById(R.id.loading);

        CardView card1 = findViewById(R.id.card1);
        card1.setOnClickListener(v -> {
            loading.setVisibility(View.VISIBLE);
            Intent intent = new Intent(MainActivity.this, ActivityPesquisarColeta.class);
            startActivity(intent);
        });

        CardView card2 = findViewById(R.id.card2);
        card2.setOnClickListener(v -> {
            loading.setVisibility(View.VISIBLE);
            Intent intent = new Intent(MainActivity.this, ProcurarCentrosDeReciclagens.class);
            startActivity(intent);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }
}