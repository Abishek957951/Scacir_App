package com.example.scacirbeta.Traducir;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.scacirbeta.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class escogerTraducir extends Fragment {
//Se inicializan todas las variables que se van a usar a lo largo del programa
    private viewModelTraducir viewModel;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;

    Button buttonCaptura, buttonGaleria;
    String currentPhotoPath;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(viewModelTraducir.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_escoger_traducir, container, false);
        buttonCaptura = view.findViewById(R.id.buttonCamera);
        buttonGaleria = view.findViewById(R.id.buttonGaleria);

        buttonCaptura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermissions();
            }
        });
        buttonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });
        return view;
    }

    //Se piden los permisos para acceder a la camara
    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(),new String[] {Manifest.permission.CAMERA},CAMERA_PERM_CODE);
        }else{
            dispatchTakePictureIntent();
        }
    }

    //Se pide permiso para accceder a la camara
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(requireContext(), "Se necesita acceder a la cámara del dispositivo móvil", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Una vez que se le da permiso para acceder a la camara
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (!Python.isStarted())
                Python.start(new AndroidPlatform(requireContext()));
            Python py = Python.getInstance();
            PyObject detecciones = py.getModule("admintraducir");

            if (requestCode == CAMERA_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    File f = new File(currentPhotoPath);
                    Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] imageBytes = stream.toByteArray();
                    try {
                        stream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    PyObject result = detecciones.callAttr("fase1", imageBytes);

                    byte[] processedImageBytes = result.toJava(byte[].class);

                    // Convert the byte array back to an Android Bitmap
                    Bitmap processedImage = BitmapFactory.decodeByteArray(processedImageBytes, 0, processedImageBytes.length);
                    viewModel.setImageDetecciones(processedImage);

                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(f);
                    mediaScanIntent.setData(contentUri);
                    requireContext().sendBroadcast(mediaScanIntent);
                }
            }
            if (requestCode == GALLERY_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    Uri contentUri = data.getData();
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri);
                    byte[] imageBytes = convertImageUriToByteArray(contentUri);

                    PyObject result = detecciones.callAttr("fase1", imageBytes);

                    byte[] processedImageBytes = result.toJava(byte[].class);

                    // Convert the byte array back to an Android Bitmap
                    Bitmap processedImage = BitmapFactory.decodeByteArray(processedImageBytes, 0, processedImageBytes.length);
                    viewModel.setImageDetecciones(processedImage);
                }
            }
        }catch(Exception e){
            Toast.makeText(requireContext(), "A ocurrido un error durante la detección, vuelve a tomar la foto" +
                    " y asegurate que este bajo el reglamento", Toast.LENGTH_LONG).show();
        }
    }

    //Funcion para convertir imagen uri a un bytearray
    private byte[] convertImageUriToByteArray(Uri imageUri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //Obtiene la extension del archivo
    private String getFileExt(Uri contentUri){
        ContentResolver c = requireContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

//Crea una imagen archivo
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //Se encarga detodo lo relacionado con el manejo de la camara
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

}