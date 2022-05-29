package com.example.myapplication;

import android.content.Intent;

import java.util.ArrayList;

public class BTPresenter implements ContractBT.PresenterBT, ContractBT.ModelBT.OnEventListener{
    private ContractBT.ViewBT mainView;
    private ContractBT.ModelBT model;

    public BTPresenter(ContractBT.ViewBT mainView, ContractBT.ModelBT model){
        this.mainView = mainView;
        this.model = model;
    }
    @Override
    public boolean isEnabled() {
        return model.isEnabled();
    }


    @Override
    public void onEventOnBT() {
        mainView.onBT();
    }

    @Override
    public void onEventShowEnabled() {
        this.mainView.showEnabled();
    }

    @Override
    public void onEventShowDisabled() {
        this.mainView.showDisabled();
    }

    @Override
    public void onEventUpdateListDevices() {
        this.mainView.updateListDevices();
    }

    @Override
    public void hideDialog() {
        this.mainView.hideDialog();
    }

    @Override
    public void onEventShowDialog() {
        this.mainView.showDialog();
    }

    @Override
    public void btnON() {
        this.model.on(this);
    }

    @Override
    public void btnOFF() {
        this.model.off(this);
    }

    @Override
    public boolean searchDevices() {
        return this.model.startDiscovery();
    }
    @Override
    public boolean cancelSearchDevices() {
        return this.model.cancelDiscovery();
    }

    @Override
    public String[] getPermissonsStablished(){
        return this.model.getPermissonsStablished();
    }
    @Override
    public ArrayList<String> getDevicesFounded(){
        return this.model.getDevicesFounded();
    }

    @Override
    public void onReceive(Intent i) {
        this.model.onReceive(this,i);
    }
    @Override
    public int get_MULTIPLE_PERMISSIONS() {
        return this.model.get_MULTIPLE_PERMISSIONS();
    }

}
