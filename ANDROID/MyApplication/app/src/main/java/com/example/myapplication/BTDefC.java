package com.example.myapplication;

import android.content.Intent;

import java.util.ArrayList;

public interface BTDefC {

    interface ViewBT {
        void onBT();
        void showDialog();
        void hideDialog();
        void updateListDevices();
        void showEnabled();
        void showDisabled();
        void showDispSelected(String nameDisp);
    }

    interface ModelBT {
        ArrayList<String> getDevicesFounded();
        boolean isEnabled();
        void off(BTDefC.ModelBT.OnEventListener listener);
        boolean startDiscovery(final BTDefC.ModelBT.OnEventListener listener);
        boolean cancelDiscovery();
        void on(BTDefC.ModelBT.OnEventListener listener);
        String[] getPermissonsStablished();
        void onReceive(BTDefC.ModelBT.OnEventListener listener, Intent action);
        int get_MULTIPLE_PERMISSIONS();
        void showDispSelected(BTDefC.ModelBT.OnEventListener listener, String str);
        String getMacSelected(BTDefC.ModelBT.OnEventListener listener);
        interface OnEventListener {
            void onEventOnBT();
            void onEventShowEnabled();
            void onEventShowDisabled();
            void onEventUpdateListDevices();
            void hideDialog();
            void onEventShowDialog();
            void onEventShowDispSelected(String str);
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
        void DispSelected(String str);
        String getMacSelected();
    }
}