package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class ConexionBTActivity extends AppCompatActivity implements BTDefC.ViewBT {
    private ListView mListView;
    private TextView txtEstado;
    private Button btnConectar;
    private Button btnActualizar;
    private ProgressDialog mProgressDlg;
    private Switch sw_bt_on_off;
    private HandlerBTP BT = null;
    private ArrayAdapter<String> mPaired;
    public static final String CODE_ADDRESS = "Direccion_Bluethoot";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt);
        this.mListView = findViewById(R.id.listVinculados);
        this.btnConectar = findViewById(R.id.btnConectarBt);
        this.btnActualizar = findViewById(R.id.btnActualizar);
        this.txtEstado = findViewById(R.id.textEstado);
        this.sw_bt_on_off = findViewById(R.id.switch_bt_on_off);
        //Se crea un adaptador para podermanejar el bluethoot del celular
        this.BT = new HandlerBTP(this,new EngineBTM());
        mProgressDlg = new ProgressDialog(this);
        mProgressDlg.setMessage("Buscando dispositivos...");
        mProgressDlg.setCancelable(false);
        //se asocia un listener al boton cancelar para la ventana de dialogo ue busca los dispositivos bluethoot
        mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", btnCancelarDialogListener);
        if (checkPermissions()) enableComponent();
    }

    protected void enableComponent() {
        //se determina si existe bluethoot en el celular
        if (!BT.isEnabled()) {
            //si el celular no soporta bluethoot
            showUnsupported();
            BT.onEventShowDisabled();
        } else {
            //si el celular soporta bluethoot, se definen los listener para los botones de la activity
            btnConectar.setOnClickListener(btnEmparejarListener);
            btnActualizar.setOnClickListener(btnBuscarListener);
            mListView.setOnItemClickListener(listClickListener);
            mPaired = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
            mListView.setAdapter(mPaired);
            BT.onEventShowEnabled();
        }
        sw_bt_on_off.setOnClickListener(switch_on_off);
        //se definen un broadcastReceiver que captura el broadcast del SO cuando captura los siguientes eventos:
        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); //Cambia el estado del Bluethoot (Acrtivado /Desactivado)
        filter.addAction(BluetoothDevice.ACTION_FOUND); //Se encuentra un dispositivo bluethoot al realizar una busqueda
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED); //Cuando se comienza una busqueda de bluethoot
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); //cuando la busqueda de bluethoot finaliza

        //se define (registra) el handler que captura los broadcast anterirmente mencionados.
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void showUnsupported() {
        txtEstado.setText("Bluetooth no es soportado por el dispositivo movil");

        BT.onEventShowDisabled();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private View.OnClickListener btnBuscarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            BT.searchDevices();
        }
    };
    private AdapterView.OnItemClickListener listClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BT.DispSelected((String) mListView.getItemAtPosition(position));
        }
    };

    //Handler que captura los brodacast que emite el SO al ocurrir los eventos del bluethoot
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            BT.onReceive(intent);
        }
    };
    //Metodo que actua como Listener de los eventos que ocurren en los componentes graficos de la activty
    private View.OnClickListener switch_on_off = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(sw_bt_on_off.isChecked()){
                BT.btnON();
            }else{
                BT.btnOFF();
            }
        }
    };
    @Override
    public void updateListDevices() {
        ArrayList<String> devicesFounded = BT.getDevicesFounded();
        mPaired.clear();
        for (String device : devicesFounded) {
            mPaired.add(device);
        }
    }

    @Override
    public void showEnabled() {
        txtEstado.setText("Bluetooth Habilitado");
        txtEstado.setTextColor(Color.BLUE);
        sw_bt_on_off.setChecked(true);
        btnConectar.setEnabled(true);
        btnActualizar.setEnabled(true);
        BT.searchDevices();
    }

    @Override
    public void showDisabled() {
        txtEstado.setText("Bluetooth Deshabilitado");
        txtEstado.setTextColor(Color.RED);
        btnConectar.setEnabled(false);
        btnActualizar.setEnabled(false);
        sw_bt_on_off.setChecked(false);
    }

    @Override
    public void showDispSelected(String nameDisp) {
        txtEstado.setText(nameDisp);
        txtEstado.setTextColor(Color.GREEN);
    }

    private View.OnClickListener btnEmparejarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String macDisp = BT.getMacSelected();
            Intent intent = new Intent(ConexionBTActivity.this, SysEmbebidoActivity.class);
            intent.putExtra(CODE_ADDRESS, macDisp);
            startActivity(intent);
        }
    };

    private DialogInterface.OnClickListener btnCancelarDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            BT.hideDialog();
            BT.cancelSearchDevices();
        }
    };


    //Metodo que chequea si estan habilitados los permisos
    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();

        //Se chequea si la version de Android es menor a la 6
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }


        for (String p:this.BT.getPermissonsStablished()) {
            result = ContextCompat.checkSelfPermission(this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),BT.get_MULTIPLE_PERMISSIONS() );
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int permiss = BT.get_MULTIPLE_PERMISSIONS();
        if(requestCode == permiss ){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permissions granted.
                enableComponent(); // Now you call here what ever you want :)
            } else {
                String perStr = "";
                for (String per : permissions) {
                    perStr += "\n" + per;
                }
                // permissions list of don't granted permission
                Toast.makeText(this, "ATENCION: La aplicacion no funcionara " +
                        "correctamente debido a la falta de Permisos\n"+perStr, Toast.LENGTH_LONG).show();
            }
            return;
        }
    }

    @Override
    public void onBT() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, 1000);
    }

    @Override
    public void showDialog() {
        mProgressDlg.show();
    }

    @Override
    public void hideDialog() {
        mProgressDlg.dismiss();
    }
}