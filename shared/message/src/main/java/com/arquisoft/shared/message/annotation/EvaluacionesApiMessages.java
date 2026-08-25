package com.arquisoft.shared.message.annotation;

public final class EvaluacionesApiMessages {

    private EvaluacionesApiMessages() {}

    public static final class Comun {

        private Comun() {}

        public static final String RESP_401 = "No autenticado";
        public static final String RESP_403 = "Sin permisos para realizar la operación";
        public static final String RESP_503 = "Servicio de persistencia no disponible";
    }

    public static final class ItemCualitativoJurado {

        private ItemCualitativoJurado() {}

        public static final String TAG_NAME = "Ítems cualitativos del jurado";
        public static final String TAG_DESCRIPTION =
                "Administración de los ítems cualitativos usados por el jurado";
        public static final String REGISTRAR_SUMMARY = "Registrar ítem cualitativo";
        public static final String REGISTRAR_DESCRIPTION =
                "Registra un nuevo ítem cualitativo disponible para el jurado";
        public static final String REGISTRAR_RESP_201 = "Ítem cualitativo registrado";
        public static final String REGISTRAR_RESP_400 = "Datos de entrada inválidos";
        public static final String REGISTRAR_RESP_422 = "El nombre del ítem ya existe";
    }
}
