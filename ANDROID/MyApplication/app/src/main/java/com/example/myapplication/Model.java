package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

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

    //VARIABLES PARA EL MANEJO DE LA CONEXIÓN BT
    final int handlerState = 0; //used to identify handler message
    private StringBuilder recDataString = new StringBuilder();
    private String address;
    private BluetoothSocket btSocket;
    Handler bluetoothIn = Handler_Msg_Hilo_Principal();
    private BluetoothAdapter btAdapter= BluetoothAdapter.getDefaultAdapter();
    // SPP UUID service  - Funciona en la mayoria de los dispositivos
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ConnectedThread mConnectedThread;
    private Presenter p;

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    public void setPresenter(Presenter p ){
        this.p = p ;
    }
    private Handler Handler_Msg_Hilo_Principal ()
    {
        return new Handler() {
            public void handleMessage(android.os.Message msg)
            {
                //si se recibio un msj del hilo secundario
                if (msg.what == handlerState)
                {
                    //voy concatenando el msj
                    String readMessage = (String) msg.obj;
                    recDataString.append(readMessage);
                    int endOfLineIndex = recDataString.indexOf("\r\n");

                    //cuando recibo toda una linea la muestro en el layout
                    if (endOfLineIndex > 0)
                    {

                        String dataInPrint = recDataString.substring(0, endOfLineIndex);
                        p.onEventVel(Integer.getInteger(dataInPrint));
                        p.onEventUmbral(Integer.getInteger(dataInPrint));
                        p.onEventTempAmbiente(Integer.getInteger(dataInPrint));
                        recDataString.delete(0, recDataString.length());
                    }
                }
            }
        };
    }

    //******************************************** Hilo secundario del Activity**************************************
    //*************************************** recibe los datos enviados por el HC05**********************************

    private class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //Constructor de la clase del hilo secundario
        public ConnectedThread(BluetoothSocket socket)
        {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try
            {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        //metodo run del hilo, que va a entrar en una espera activa para recibir los msjs del HC05
        public void run()
        {
            byte[] buffer = new byte[256];
            int bytes;

            //el hilo secundario se queda esperando mensajes del HC05
            while (true)
            {
                try
                {
                    //se leen los datos del Bluethoot
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);

                    //se muestran en el layout de la activity, utilizando el handler del hilo
                    // principal antes mencionado
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }


        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                //showToast("La conexion fallo");
                //finish();

            }
        }
    }

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
        /*handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                mConnectedThread.write("0");
                listener.onEventTempAmbiente(tempActual); // función para refrescar temperatura ambiente
                tempActual++;
                handler.postDelayed(this, intervalTempoMillis);
            }

        }, intervalTempoMillis);*/
    }

    @Override
    public boolean conectarBT(final OnEventListener listener, String address) {
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        boolean conectado = false;
        //se realiza la conexion del Bluethoot crea y se conectandose a atraves de un socket
        try
        {
            btSocket = createBluetoothSocket(device);
            btSocket.connect();
            conectado = true;
        }catch (IOException e){
            try {
                btSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        //Una establecida la conexion con el Hc05 se crea el hilo secundario, el cual va a recibir
        // los datos de Arduino atraves del bluethoot
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
        return conectado;
    }
}