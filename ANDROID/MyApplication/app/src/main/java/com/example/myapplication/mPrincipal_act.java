package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.os.Handler;

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

    private String address;
    /*
    Handler bluetoothIn;
    final int handlerState = 0; //used to identify handler message
    private StringBuilder recDataString = new StringBuilder();
    private BluetoothAdapter btAdapter;
    private BluetoothSocket btSocket;
    // SPP UUID service  - Funciona en la mayoria de los dispositivos
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ConnectedThread mConnectedThread;
    */
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
        Model m = new Model();
        this.p                  = new Presenter(this,m);
        m.setPresenter(p);
        //bluetoothIn = Handler_Msg_Hilo_Principal();
        //btAdapter = BluetoothAdapter.getDefaultAdapter();
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
    //Cada vez que se detecta el evento OnResume se establece la comunicacion con el HC05, creando un
    //socketBluethoot
    public void onResume() {
        super.onResume();
        Intent intent=getIntent();
        Bundle extras=intent.getExtras();
        address= extras.getString("Direccion_Bluethoot");
        boolean conectado = p.conectarBT(address);
        //Obtengo el parametro, aplicando un Bundle, que me indica la Mac Adress del HC05
        showToast( "BT conectado ? :  "+conectado);
        /*BluetoothDevice device = btAdapter.getRemoteDevice(address);

        //se realiza la conexion del Bluethoot crea y se conectandose a atraves de un socket
        try
        {
            btSocket = createBluetoothSocket(device);
            showToast("conectado! ");
        }
        catch (IOException e)
        {
            showToast( "La creacción del Socket fallo "+address);
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        }
        catch (IOException e)
        {
            try
            {
                btSocket.close();
            }
            catch (IOException e2)
            {
                //insert code to deal with this
            }
        }

        //Una establecida la conexion con el Hc05 se crea el hilo secundario, el cual va a recibir
        // los datos de Arduino atraves del bluethoot
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
        */
        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        //mConnectedThread.write("x");
    }
    //Metodo que crea el socket bluethoot
    /*private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }*/
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

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    /*
    //Handler que sirve que permite mostrar datos en el Layout al hilo secundario
    @SuppressLint("HandlerLeak")
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
                        mostrarTempAmbiente(Integer.getInteger(dataInPrint));

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
                showToast("La conexion fallo");
                finish();

            }
        }
    }*/
}