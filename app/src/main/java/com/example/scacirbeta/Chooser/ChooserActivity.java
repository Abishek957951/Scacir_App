package com.example.scacirbeta.Chooser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scacirbeta.Menu.MenuPrincipal;
import com.example.scacirbeta.R;
import com.example.scacirbeta.Traducir.TraducirActivity;


public class ChooserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);
    }

    public void onGoToVolver(View view){
        Intent intent = new Intent(this, MenuPrincipal.class);
        startActivity(intent);
        finish();
    }

    public void onGoToTraducir(View view){
        Intent intent = new Intent(this, TraducirActivity.class);
        startActivity(intent);
        finish();
    }

    public void onGoToComparar(View view){
        Toast.makeText(this, "Función por implementar...", Toast.LENGTH_SHORT).show();
    }

}