package com.arquisoft.shared.message.annotation;

public final class EntregablesApiMessages {

    private EntregablesApiMessages() {}

    public static final class Comun {

        private Comun() {}

        public static final String RESP_401 = "No autenticado";
        public static final String RESP_403 = "Sin permisos para realizar la operación";
    }

    public static final class EstadoEntregable {

        private EstadoEntregable() {}

        public static final String TAG_NAME = "Estados de entregable";
        public static final String TAG_DESCRIPTION = "Catálogo de estados que puede tener un entregable";
        public static final String CONSULTAR_SUMMARY = "Consultar los estados de entregable";
        public static final String CONSULTAR_DESCRIPTION =
                "Devuelve el catálogo completo de estados de entregable con su identificador, "
                        + "nombre y descripción";
        public static final String CONSULTAR_RESP_200 = "Catálogo de estados de entregable";
    }
}
