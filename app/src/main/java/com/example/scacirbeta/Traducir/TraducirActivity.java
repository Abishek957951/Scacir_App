package com.example.scacirbeta.Traducir;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.scacirbeta.Chooser.ChooserActivity;
import com.example.scacirbeta.Menu.MenuPrincipal;
import com.example.scacirbeta.R;

public class TraducirActivity extends AppCompatActivity {

    ViewPager2 viewPager2;
    administradorTraducir admin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traducir);
        viewPager2 = findViewById(R.id.view_pager);
        admin = new administradorTraducir(this);
        viewPager2.setAdapter(admin);
    }

    public void onGoToSalir(View view){
        Intent intent = new Intent(this, ChooserActivity.class);
        startActivity(intent);
        finish();
    }
}