package com.example.scacirbeta.Traducir;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class viewModelTraducir extends ViewModel {

    private final MutableLiveData<Bitmap> imagenDetecciones = new MutableLiveData<>();

    private final MutableLiveData<byte[]> imagenEsquema = new MutableLiveData<>();

    public void setImageDetecciones(Bitmap bitmap) {
        imagenDetecciones.setValue(bitmap);
    }

    public LiveData<Bitmap> getImageBitmap() {
        return imagenDetecciones;
    }

    public void setImageEsquema(byte[] byte_svg) {
        imagenEsquema.setValue(byte_svg);
    }

    public LiveData<byte[]> getImageBitmapEsquema() {
        return imagenEsquema;
    }






}
