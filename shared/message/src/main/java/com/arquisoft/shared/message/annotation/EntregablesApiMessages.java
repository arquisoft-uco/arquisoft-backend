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

    public static final class EntregableProyectoGrado {

        private EntregableProyectoGrado() {}

        public static final String TAG_NAME = "Entregables de proyecto de grado";
        public static final String TAG_DESCRIPTION = "Generación de entregables de un proyecto de grado y cambio de su estado";
        public static final String GENERAR_SUMMARY = "Generar un entregable de un proyecto de grado";
        public static final String GENERAR_DESCRIPTION =
                "El asesor genera un nuevo entregable del proyecto de grado, que queda disponible para evaluación. "
                        + "La versión es la siguiente a la del último entregable del proyecto y el asesor se toma del token";
        public static final String GENERAR_RESP_201 = "Entregable generado; devuelve su identificador";
        public static final String GENERAR_RESP_400 = "El identificador del proyecto de grado no es un UUID válido";
        public static final String GENERAR_RESP_422 = "El proyecto de grado no existe o el asesor no está vigente";
        public static final String CAMBIAR_ESTADO_SUMMARY = "Cambiar el estado de un entregable de proyecto de grado";
        public static final String CAMBIAR_ESTADO_DESCRIPTION =
                "El asesor cambia el estado de un entregable a Cancelado o Cerrado. "
                        + "Solo puede cerrarse un entregable Aprobado o Aprobado Con Observaciones";
        public static final String CAMBIAR_ESTADO_RESP_204 = "Estado del entregable cambiado";
        public static final String CAMBIAR_ESTADO_RESP_400 =
                "El identificador del entregable no es un UUID válido o el estado viene vacío";
        public static final String CAMBIAR_ESTADO_RESP_422 =
                "El entregable no existe, el estado no existe o la transición no está permitida";
    }
}
