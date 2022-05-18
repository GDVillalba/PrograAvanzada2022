package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

public class mPrincipal_act extends Activity implements Contract.View{
    private ImageButton btnAumentarVel;
    private ImageButton btnDisminuirVel;
    private ImageButton btnAumentarTermo;
    private ImageButton btnDisminuirTermo;
    private Presenter p;
    private TextView txtV;
    private TextView txtT;
    private TextView txtTmp;
    private Switch off_on;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mprincipal);
        this.btnAumentarTermo   = findViewById(R.id.btnAumentarT);
        this.btnDisminuirTermo  = findViewById(R.id.btnDisminuirT);
        this.btnAumentarVel     = findViewById(R.id.btnAumentarV);
        this.btnDisminuirVel    = findViewById(R.id.btnDismV);
        this.txtT               = findViewById(R.id.txtUmbral);
        this.txtV               = findViewById(R.id.txtVel);
        this.txtTmp             = findViewById(R.id.txtTempAmb);
        this.off_on             = findViewById(R.id.off_on);
        this.p                  = new Presenter(this,new Model());

        this.off_on.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(off_on.isChecked()){
                    p.encender();
                }else{
                    p.apagar();
                }
            }
        });
        this.btnAumentarTermo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p.btnAumentarT();
            }
        });
        this.btnDisminuirTermo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p.btnDisminuirT();
            }
        });
        this.btnAumentarVel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p.btnAumentarV();
            }
        });
        this.btnDisminuirVel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p.btnDisminuirV();
            }
        });

    }

    @Override
    public void mostrarVel(int str){
        this.txtV.setText(Integer.toString(str));
    }
    @Override
    public void mostrarUmbralTermo(int str){
        this.txtT.setText(Integer.toString(str));
    }
    @Override
    public void mostrarTempAmbiente(int str){
        this.txtTmp.setText(Integer.toString(str));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        p.onDestroy();
    }
}