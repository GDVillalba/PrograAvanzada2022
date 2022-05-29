package com.example.myapplication;

import android.content.Intent;

import java.util.ArrayList;

public interface ContractBT {

    interface ViewBT {
        void onBT();

        void showDialog();

        void hideDialog();

        void updateListDevices();

        void showEnabled();

        void showDisabled();
    }

    interface ModelBT {
        ArrayList<String> getDevicesFounded();
        boolean isEnabled();
        void off(ContractBT.ModelBT.OnEventListener listener);
        boolean startDiscovery();
        boolean cancelDiscovery();
        void on(ContractBT.ModelBT.OnEventListener listener);
        String[] getPermissonsStablished();
        void onReceive(ContractBT.ModelBT.OnEventListener listener, Intent action);
        int get_MULTIPLE_PERMISSIONS();

        interface OnEventListener {
            void onEventOnBT();
            void onEventShowEnabled();
            void onEventShowDisabled();
            void onEventUpdateListDevices();
            void hideDialog();
            void onEventShowDialog();
        }
    }

    interface PresenterBT {
        boolean isEnabled();
        void btnON();
        void btnOFF();
        boolean searchDevices();
        boolean cancelSearchDevices();
        String[] getPermissonsStablished();
        ArrayList<String> getDevicesFounded();
        void onReceive(Intent action);
        int get_MULTIPLE_PERMISSIONS();
    }
}