package com.example.myapplication;


public interface SysEmbebidoC {

    interface View {
        void mostrarUmbralTermo(String string);
        void mostrarVel(String string);
        void mostrarTempAmbiente(String string);
        void showToast(String str);
    }

    interface Model {

        void aumentarVel(SysEmbebidoC.Model.OnEventListener listener);
        void disminuirUmbralTermo(SysEmbebidoC.Model.OnEventListener listener);
        void aumentarUmbralTermo(SysEmbebidoC.Model.OnEventListener listener);
        void apagarSys(SysEmbebidoC.Model.OnEventListener listener);
        void encenderSys(SysEmbebidoC.Model.OnEventListener listener);
        boolean conectarBT(SysEmbebidoC.Model.OnEventListener listener, String address);
        void onDestroy(SysEmbebidoC.Model.OnEventListener listener);
        boolean isBTConnectado(SysEmbebidoC.Model.OnEventListener listener);
        interface OnEventListener {
            void onEventVel(String string);
            void onEventTempAmbiente(String string);
            void onEventUmbral(String string);
            void alert(String s);
        }
    }

    interface Presenter {
        boolean isBTConnectado();
        void btnAumentarT();
        void btnDisminuirT();
        void btnAumentarV();
        void encender();
        void apagar();
        void onDestroy();
        boolean conectarBT(String address);
    }
}