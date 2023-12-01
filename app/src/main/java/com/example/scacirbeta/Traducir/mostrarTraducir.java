package com.example.scacirbeta.Traducir;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.scacirbeta.R;
import com.github.chrisbanes.photoview.PhotoView;

public class mostrarTraducir extends Fragment {

    private viewModelTraducir viewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(viewModelTraducir.class);


        PhotoView imageView = (PhotoView) view.findViewById(R.id.muestraDetecciones);
        //ImageView imageView = view.findViewById(R.id.muestraDetecciones);

        viewModel.getImageBitmap().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mostrar_traducir, container, false);
    }
}