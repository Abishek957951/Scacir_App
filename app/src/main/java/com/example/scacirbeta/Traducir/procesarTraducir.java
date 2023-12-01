package com.example.scacirbeta.Traducir;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.scacirbeta.R;

public class procesarTraducir extends Fragment {

    private viewModelTraducir viewModel;

    EditText editText;

    String valores_com;

    Button buttonProcesar;
    FrameLayout progressBarContainer;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(viewModelTraducir.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_procesar_traducir, container, false);
        progressBarContainer = view.findViewById(R.id.progressBarContainer);
        editText = view.findViewById(R.id.textoEtiquetas);
        buttonProcesar = view.findViewById(R.id.buttonProcesar);
        buttonProcesar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                procesamineto();
            }
        });
        buttonProcesar.setEnabled(true);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                 if(!s.toString().matches("[a-zA-Z0-9 ]+")){
                    editText.setError("Solo se permiten letras, números y espacios");
                    buttonProcesar.setEnabled(false);
                }else{
                    editText.setError(null); // Limpia el error
                    buttonProcesar.setEnabled(true); // Habilita el botón si todo está correcto
                }
            }
        });
        return view;
    }

    private void procesamineto(){
        progressBarContainer.setVisibility(View.VISIBLE);
        valores_com = editText.getText().toString();
        System.out.println("Se inicia el metodo de procesamiento");
        try {
            if (!Python.isStarted())
                Python.start(new AndroidPlatform(requireContext()));
            Python pyt = Python.getInstance();
            PyObject esquema = pyt.getModule("admintraducir");
            PyObject result = esquema.callAttr("fase2", valores_com);

            PyObject diccionario1 = result.asList().get(0);
            PyObject diccionario2 = result.asList().get(1);
            PyObject byteArray = result.asList().get(2);
            PyObject list = result.asList().get(3);
            PyObject primero = result.asList().get(4);

// Para convertir los diccionarios PyObject a Map en Java (si es necesario)
            System.out.println(diccionario1.asMap());
            System.out.println(diccionario2.asMap());
            System.out.println(list.asList());
            System.out.println(primero.asList());

// Para convertir el byte array a un tipo de dato utilizable en Java
        byte[] processedImageBytes = byteArray.toJava(byte[].class);

            //byte[] processedImageBytes = result.toJava(byte[].class);
            viewModel.setImageEsquema(processedImageBytes);
            System.out.println("Se completa el metodo de procesamiento");
            progressBarContainer.setVisibility(View.GONE);
        }catch(Exception e){
            progressBarContainer.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "A ocurrido un error durante el procesamiento, vuelve a tomar la foto" +
                                                    " y asegurate que este bajo el reglamento", Toast.LENGTH_LONG).show();
        }
    }
}