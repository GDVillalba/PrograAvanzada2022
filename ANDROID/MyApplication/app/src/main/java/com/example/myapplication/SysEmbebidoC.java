package com.example.myapplication;


public interface SysEmbebidoC {

    interface View {
        void reposar();
        void activar();
        void mostrarUmbralTermo(String string);
        void mostrarVel(String string);
        void mostrarTempAmbiente(String string);
        void showToast(String str);
    }

    interface Model {
        void apagar(final OnEventListener listener);
        void encender(final OnEventListener listener);
        void inicializarValores(SysEmbebidoC.Model.OnEventListener listener);
        void aumentarVel(SysEmbebidoC.Model.OnEventListener listener);
        void disminuirUmbralTermo(SysEmbebidoC.Model.OnEventListener listener);
        void aumentarUmbralTermo(SysEmbebidoC.Model.OnEventListener listener);
        void apagarSys(SysEmbebidoC.Model.OnEventListener listener);
        void encenderSys(SysEmbebidoC.Model.OnEventListener listener);
        boolean conectarBT(SysEmbebidoC.Model.OnEventListener listener, String address);
        void onDestroy(SysEmbebidoC.Model.OnEventListener listener);
        boolean isBTConnectado(SysEmbebidoC.Model.OnEventListener listener);
        interface OnEventListener {
            void onEventReposar();
            void onEventActivar();
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
        void btnEncender();
        void btnApagar();
        void iniciarSys();
        void reposarSys();
        void activarSys();
        void onDestroy();
        boolean conectarBT(String address);
    }
}