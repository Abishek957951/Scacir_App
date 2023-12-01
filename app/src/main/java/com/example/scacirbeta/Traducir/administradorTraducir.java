package com.example.scacirbeta.Traducir;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class administradorTraducir extends FragmentStateAdapter {
    public administradorTraducir(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new introTraducir();
            case 1:
                return new escogerTraducir();
            case 2:
                return new mostrarTraducir();
            case 3:
                return new procesarTraducir();
            case 4:
                return new finTraducir();
            default:
                return new introTraducir();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
