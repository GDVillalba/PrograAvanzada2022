package com.example.myapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Set;

public class EngineBTM implements BTDefC.ModelBT{
    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.
    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private String[] permissions = new String[]{
            Manifest.permission.BLUETOOTH,
            //Manifest.permission.BLUETOOTH_SCAN,
            //Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    private String macDisp= "";

    @Override
    public ArrayList<String> getDevicesFounded(){
        ArrayList<String> devices = new ArrayList<String>();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            devices.add(device.getName()+" Vinculado\n"+device.getAddress());
        }
        return  devices;
    }

    @Override
    public boolean isEnabled(){
        return btAdapter.isEnabled();
    }
    @Override
    public void off(final BTDefC.ModelBT.OnEventListener listener){
        btAdapter.disable();
        listener.onEventShowDisabled();
    }
    @Override
    public boolean startDiscovery(final OnEventListener listener){
        listener.onEventShowDialog();
        return  btAdapter.startDiscovery();
    }
    @Override
    public boolean cancelDiscovery(){
        return btAdapter.cancelDiscovery();
    }
    @Override
    public void on(final BTDefC.ModelBT.OnEventListener listener){
        listener.onEventOnBT();
    }
    @Override
    public String[] getPermissonsStablished() {
        return permissions;
    }

    @Override
    public void onReceive(final BTDefC.ModelBT.OnEventListener listener, Intent intent) {
        //Atraves del Intent obtengo el evento de Bluethoot que informo el broadcast del SO
        String action = intent.getAction();

        //Si cambio de estado el Bluethoot(Activado/desactivado)
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
            //Obtengo el parametro, aplicando un Bundle, que me indica el estado del Bluethoot
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

            //Si esta activado
            if (state == BluetoothAdapter.STATE_ON){
                listener.onEventShowEnabled();
            }else if (state == BluetoothAdapter.STATE_OFF){
                listener.onEventShowDisabled();
            }
        }
        if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
            listener.onEventShowDialog();
        }
        if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
        {
            listener.onEventUpdateListDevices();
            listener.hideDialog();
        }
    }

    @Override
    public int get_MULTIPLE_PERMISSIONS() {
        return this.MULTIPLE_PERMISSIONS;
    }

    @Override
    public void showDispSelected(OnEventListener listener, String str) {
        String[] str1 = str.split("\n");
        this.macDisp = str1[1];
        listener.onEventShowDispSelected(str1[0]);
    }

    @Override
    public String getMacSelected(OnEventListener listener) {
        return macDisp;
    }
}
