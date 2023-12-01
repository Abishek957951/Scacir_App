package com.example.scacirbeta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.scacirbeta.Menu.MenuPrincipal;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onGotoMenu(View view){
        Intent intent = new Intent(this, MenuPrincipal.class);
        startActivity(intent);
    }
}