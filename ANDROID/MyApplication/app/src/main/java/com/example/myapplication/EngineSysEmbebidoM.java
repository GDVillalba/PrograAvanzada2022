package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class EngineSysEmbebidoM implements SysEmbebidoC.Model {
    /*
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
    */

    public static final String CODE_TEMPERATURA ="0";
    public static final String CODE_ON_OFF="1";
    public static final String CODE_AUMENTAR_VEL="2";
    public static final String CODE_DISMINUIR_UMBRAL="3";
    public static final String CODE_AUMENTAR_UMBRAL="4";
    public static final String CODE_INIT ="5";
    public static final String CODE_REPOSO ="0";
    public static final String CODE_ACTIVO ="1";
    private boolean sysOn =true;
    private boolean corriendo = true;

    //VARIABLES PARA EL MANEJO DE LA CONEXIÓN EngineBTM
    final int handlerState = 0; //used to identify handler message
    private static StringBuilder recDataString = new StringBuilder();
    private String address;
    private BluetoothSocket btSocket;
    Handler bluetoothIn = Handler_Msg_Hilo_Principal();
    private BluetoothAdapter btAdapter= BluetoothAdapter.getDefaultAdapter();
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ConnectedThread mConnectedThread;
    private HandlerSysEmbebidoP p;
    private boolean socketConectado = false;

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    public void setPresenter(HandlerSysEmbebidoP p ){
        this.p = p ;
    }
    private Handler Handler_Msg_Hilo_Principal ()
    {
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                //si se recibio un msj del hilo secundario
                if (msg.what == handlerState) {
                    //voy concatenando el msj
                    String readMessage = (String) msg.obj;
                    recDataString.append(readMessage);
                    int endOfLineIndex = recDataString.indexOf("~");


                    //cuando recibo toda una linea la muestro en el layout
                    if (endOfLineIndex > 0) {
                        String dataInPrint;
                        while (endOfLineIndex > 0) {

                            dataInPrint = new String(recDataString.substring(0, endOfLineIndex));
                            switch (dataInPrint.substring(0, 1)) {
                                case CODE_TEMPERATURA:
                                    p.onEventTempAmbiente(dataInPrint.substring(1));
                                    break;
                                case CODE_ON_OFF:
                                    if (dataInPrint.substring(1, 2).compareTo(CODE_REPOSO) == 0)
                                        p.onEventReposar();
                                    if (dataInPrint.substring(1, 2).compareTo(CODE_ACTIVO) == 0) {
                                        p.onEventActivar();
                                    }
                                    break;
                                case CODE_AUMENTAR_VEL:
                                    p.onEventVel(dataInPrint.substring(1));
                                    break;
                                case CODE_DISMINUIR_UMBRAL:
                                    p.onEventUmbral(dataInPrint.substring(1));
                                    break;
                                case CODE_AUMENTAR_UMBRAL:
                                    p.onEventUmbral(dataInPrint.substring(1));
                                    break;
                                default:
                                    break;
                            }
                            //saca del inicio de recDataString la orden ya ejecutada
                            recDataString.delete(0, endOfLineIndex + 1);
                            endOfLineIndex = recDataString.indexOf("~");
                        }
                    }
                }
            }
        };
        return handler;
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

        //metodo run del hilo, que va a entrar en una espera activa para recibir los msjs del HC05 / HC06
        public void run()
        {
            //Pide al arduino los estados iniciales de temperatura, termostato , velocidad y estado
            p.iniciarSys();

            byte[] buffer = new byte[256];
            int bytes;

            //el hilo secundario se queda esperando mensajes del HC05 / HC06
            while (corriendo)
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
                    p.alert("Error inesperado al recibir\n respuesta del Dispositivo BT!\n"+e);
                }
            }
        }


        //write method
        public void write(String input,final OnEventListener listener) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                listener.alert("Error al enviar el mensaje al dispositivo BT!\n"+e);
            }
        }
    }

    @Override
    public void inicializarValores(final OnEventListener listener) {
        if(sysIsOn(listener)){
            mConnectedThread.write(CODE_INIT,listener);
        }
    }

    @Override
    public void aumentarVel(final OnEventListener listener) {
        if(sysIsOn(listener)){
            mConnectedThread.write(CODE_AUMENTAR_VEL,listener);
        }
    }

    @Override
    public void disminuirUmbralTermo(final OnEventListener listener) {
        if(sysIsOn(listener)){
            mConnectedThread.write(CODE_DISMINUIR_UMBRAL,listener);
        }
    }

    @Override
    public void aumentarUmbralTermo(final OnEventListener listener) {
        if(sysIsOn(listener)){
            mConnectedThread.write(CODE_AUMENTAR_UMBRAL,listener);
        }
    }

    @Override
    public void apagar(final OnEventListener listener) {
        mConnectedThread.write(CODE_ON_OFF,listener);
    }

    @Override
    public void encender(final OnEventListener listener) {
        mConnectedThread.write(CODE_ON_OFF,listener);
    }

    @Override
    public void apagarSys(final OnEventListener listener) {
        sysOn = false;
    }

    @Override
    public void encenderSys(final OnEventListener listener) {
        sysOn = true;
    }

    @Override
    public boolean conectarBT(final OnEventListener listener, String address) {
        this.address = address;
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        //se realiza la conexion del Bluethoot crea y se conectandose a atraves de un socket
        try{
            btSocket = createBluetoothSocket(device);
            btSocket.connect();
            socketConectado = true;
        }catch (IOException e){

            listener.alert("Conexión con dispositivo BT fallida!");

            try {
                btSocket.close();
            } catch (IOException ex) {
                listener.alert("No pudo resolver el cierre final de conexión BT");
            }
        }

        //Una establecida la conexion con el Hc05 se crea el hilo secundario, el cual va a recibir
        // los datos de Arduino atraves del bluethoot
        if(socketConectado){
            mConnectedThread = new ConnectedThread(btSocket);
            mConnectedThread.start();
        }
        return btSocket.isConnected();
    }

    @Override
    public void onDestroy(final OnEventListener listener) {
        this.corriendo = false;
        this.socketConectado = false;
        try {
            btSocket.close();
        } catch (IOException ex) {
            listener.alert("El Sistema no pudo cerrarse correctamente ! "+ex);
        }
    }

    @Override
    public boolean isBTConnectado(OnEventListener listener) {
        return socketConectado;
    }

    public boolean sysIsOn(final OnEventListener listener){
        if(!sysOn){
            listener.alert("El Sistema se encuentra apagado !");
        }
        return sysOn;
    }
}