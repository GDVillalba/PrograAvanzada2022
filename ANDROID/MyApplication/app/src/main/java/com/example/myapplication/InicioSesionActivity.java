package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InicioSesionActivity extends Activity{
    private Button btnIngresar;
    private TextView labelEmail;
    private TextView labelPass;
    HandlerSysEmbebidoP handlerSysEmbebidoP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.btnIngresar = findViewById(R.id.btnIngresar);
        this.btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(InicioSesionActivity.this, ConexionBTActivity.class);
                //se inicia la activity de ingreso session
                startActivity(intent);
            }
        });
    }
}