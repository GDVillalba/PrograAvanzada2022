package com.example.myapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorProximidad implements SensorEventListener {
    private final SysEmbebidoC.View view;
    private SensorManager mSensorManager;
    public SensorProximidad(SysEmbebidoC.View v){
        this.view = v;
        mSensorManager = view.getSensorManager();
        Ini_Sensores();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {

            String txt="";
            if(event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                if(event.values[0] == 0)
                    view.receiveSensor();
            }
        }
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {

    }

    // Metodo para iniciar el acceso a los sensores
    public void Ini_Sensores()
    {
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_PROXIMITY),       SensorManager.SENSOR_DELAY_NORMAL);
    }

    // Metodo para parar la escucha de los sensores
    public void Parar_Sensores()
    {
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_PROXIMITY));
    }
}