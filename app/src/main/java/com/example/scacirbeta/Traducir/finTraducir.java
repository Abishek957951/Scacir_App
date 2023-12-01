package com.example.scacirbeta.Traducir;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.scacirbeta.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class finTraducir extends Fragment {

    private viewModelTraducir viewModel;
    Button descargar;

    PhotoView photoView;
    Bitmap bitmap;
    Drawable drawable;
    FrameLayout progressBarContainer;
    ExecutorService executorService = Executors.newSingleThreadExecutor();


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(viewModelTraducir.class);
        progressBarContainer = view.findViewById(R.id.progressBarContainer);

        viewModel.getImageBitmapEsquema().observe(getViewLifecycleOwner(), new Observer<byte[]>() {
            @Override
            public void onChanged(byte[] byte_svg) {
                if (byte_svg != null) {
                    try {
                        InputStream inputStream = new ByteArrayInputStream(byte_svg);
                        SVG svg = SVG.getFromInputStream(inputStream);
                        drawable = new PictureDrawable(svg.renderToPicture());
                        photoView.setImageDrawable(drawable);
                        inputStream.close();
                    } catch (SVGParseException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_fin_traducir, container, false);
        photoView = view.findViewById(R.id.muestraEsquema);
        descargar = view.findViewById(R.id.buttonDescargar);
        descargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descargamiento();
            }
        });
        return view;
    }

    private void descargamiento() {
        progressBarContainer.setVisibility(View.VISIBLE);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (drawable instanceof BitmapDrawable) {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                        if (bitmapDrawable.getBitmap() != null) {
                            bitmap = bitmapDrawable.getBitmap();
                        }
                    } else {
                        bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        canvas.drawColor(0xFFFFFFFF);
                        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        drawable.draw(canvas);
                    }
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = timeStamp + ".png";
                    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    File file = new File(storageDir, imageFileName);
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.close();

                    // Escaneo de medios para hacer el archivo visible en la galería
                    MediaScannerConnection.scanFile(requireContext(), new String[]{file.getAbsolutePath()}, null, null);

                    // Actualizar la UI después de guardar el archivo
                    progressBarContainer.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBarContainer.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), "Imagen guardada", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    // Actualizar la UI en caso de error
                    progressBarContainer.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBarContainer.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

}