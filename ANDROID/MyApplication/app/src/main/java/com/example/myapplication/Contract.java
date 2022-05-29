package com.example.myapplication;


public interface Contract {

    interface View {
        void mostrarUmbralTermo(int string);
        void mostrarVel(int string);
        void mostrarTempAmbiente(int string);
    }

    interface Model {

        void disminuirVel(Contract.Model.OnEventListener listener);

        void aumentarVel(Contract.Model.OnEventListener listener);

        void disminuirUmbralTermo(Contract.Model.OnEventListener listener);

        void aumentarUmbralTermo(Contract.Model.OnEventListener listener);

        void apagarSys(Contract.Model.OnEventListener listener);
        void encenderSys(Contract.Model.OnEventListener listener);

        void getTempAmbiente(Contract.Model.OnEventListener listener);

        interface OnEventListener {
            void onEventVel(int string);
            void onEventTempAmbiente(int string);
            void onEventUmbral(int string);
        }
    }

    interface Presenter {
        void btnAumentarT();
        void btnDisminuirT();
        void btnAumentarV();
        void btnDisminuirV();
        void encender();
        void apagar();
        void onDestroy();
    }
}