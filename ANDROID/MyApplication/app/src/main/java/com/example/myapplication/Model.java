package com.example.myapplication;

import android.os.Handler;

public class Model implements Contract.Model {

    private final int MAX_UMBRAL = 34;
    private final int MIN_UMBRAL = 18;
    private final int UMBRAL_INICIAL = 24;
    private final int  VEL_MAX = 3;
    private final int  VEL_MIN = 1;
    private final int intervalTempoMillis = 1000;

    private Handler handler = new Handler(); // En esta zona creamos el objeto Handler
    private int umbralTermo= UMBRAL_INICIAL;
    private int tempActual;
    private int vel=VEL_MIN;
    private boolean encendido=false;

    @Override
    public void disminuirVel(Contract.Model.OnEventListener listener) {
        if(encendido){
            vel--;
            if(vel < VEL_MIN) vel = VEL_MIN;
            listener.onEventVel(vel);
        }

    }

    @Override
    public void aumentarVel(final OnEventListener listener) {
        if(encendido){
            vel++;
            if(vel > VEL_MAX) vel = VEL_MAX;
            listener.onEventVel(vel);
        }

    }

    @Override
    public void disminuirUmbralTermo(final OnEventListener listener) {
        if(encendido){
            umbralTermo--;
            if(umbralTermo < MIN_UMBRAL) umbralTermo = MIN_UMBRAL;
            listener.onEventUmbral(umbralTermo);
        }
    }

    @Override
    public void aumentarUmbralTermo(final OnEventListener listener) {
        if(encendido){
            umbralTermo++;
            if(umbralTermo > MAX_UMBRAL) umbralTermo = MAX_UMBRAL;
            listener.onEventUmbral(umbralTermo);
        }

    }
    @Override
    public void apagarSys(final OnEventListener listener) {
        listener.onEventUmbral(0);
        listener.onEventVel(0);
        encendido = false;
    }

    @Override
    public void encenderSys(final OnEventListener listener) {
        this.umbralTermo = UMBRAL_INICIAL;
        this.vel=VEL_MIN;
        listener.onEventUmbral(umbralTermo);
        listener.onEventVel(vel);
        encendido = true;
    }

    @Override
    public void getTempAmbiente(final OnEventListener listener) {
        //tomar valor del sensor tmp36
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listener.onEventTempAmbiente(tempActual); // función para refrescar temperatura ambiente
                tempActual++;
                handler.postDelayed(this, intervalTempoMillis);
            }

        }, intervalTempoMillis);

    }
}