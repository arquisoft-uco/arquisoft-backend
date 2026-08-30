package com.arquisoft.shared.message.annotation;

public final class EvaluacionesApiMessages {

    private EvaluacionesApiMessages() {}

    public static final class Comun {

        private Comun() {}

        public static final String RESP_401 = "No autenticado";
        public static final String RESP_403 = "Sin permisos para realizar la operación";
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
        public static final String MODIFICAR_SUMMARY = "Modificar descripción del ítem cualitativo";
        public static final String MODIFICAR_DESCRIPTION =
                "Modifica la descripción de un ítem cualitativo existente del jurado";
        public static final String MODIFICAR_RESP_204 = "Descripción actualizada";
        public static final String MODIFICAR_RESP_400 = "Datos de entrada inválidos";
        public static final String MODIFICAR_RESP_422 = "El ítem cualitativo no existe";
    }
}
