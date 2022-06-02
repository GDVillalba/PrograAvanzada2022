package com.example.myapplication;

import android.bluetooth.BluetoothDevice;

import java.io.IOException;

public class Presenter implements Contract.Presenter, Contract.Model.OnEventListener{

    private Contract.View mainView;
    private Contract.Model model;

    public Presenter(Contract.View mainView, Contract.Model model){
        this.mainView = mainView;
        this.model = model;
        this.model.getTempAmbiente(this);
    }

    @Override
    public void btnAumentarT() {
        model.aumentarUmbralTermo(this);
    }

    @Override
    public void btnDisminuirT() {
        model.disminuirUmbralTermo(this);
    }

    @Override
    public void btnAumentarV() {
        model.aumentarVel(this);
    }

    @Override
    public void btnDisminuirV() {
        model.disminuirVel(this);
    }

    @Override
    public void encender() {
        model.encenderSys(this);
    }

    @Override
    public void apagar() {
        model.apagarSys(this);
    }

    @Override
    public void onDestroy() {
        mainView = null;
    }

    @Override
    public boolean conectarBT(String address) {
        return this.model.conectarBT(this,address);
    }

    @Override
    public void onEventVel(int string){
        if(mainView != null){
            mainView.mostrarVel(string);
        }
    }

    @Override
    public void onEventTempAmbiente(int string) {
        if(mainView != null){
            mainView.mostrarTempAmbiente(string);
        }
    }
    @Override
     public void onEventUmbral(int string) {
        if(mainView != null){
            mainView.mostrarUmbralTermo(string);
        }
    }
}