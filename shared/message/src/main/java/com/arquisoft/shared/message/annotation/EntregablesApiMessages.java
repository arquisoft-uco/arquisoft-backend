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
        public static final String TAG_DESCRIPTION = "Generación, consulta y cambio de estado de los entregables de un proyecto de grado";
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
        public static final String CONSULTAR_ASESOR_SUMMARY =
                "Filtrar los entregables generados del asesor autenticado";
        public static final String CONSULTAR_ASESOR_DESCRIPTION =
                "El asesor lista, de forma paginada, solo los entregables generados que son suyos y los filtra por "
                        + "proyecto de grado y estado, entre otros campos. Cada elemento trae la versión, el proyecto, "
                        + "el asesor y el estado";
        public static final String CONSULTAR_ASESOR_RESP_200 = "Página de entregables de proyecto de grado";
        public static final String CONSULTAR_ASESOR_RESP_400 =
                "Un campo de filtro u orden no es válido, un operador no aplica al campo, un identificador no es un UUID "
                        + "o el tamaño de página está fuera de rango";
        public static final String CONSULTAR_JURADO_SUMMARY = "Filtrar los entregables que deben ser evaluados";
        public static final String CONSULTAR_JURADO_DESCRIPTION =
                "El jurado lista, de forma paginada, los entregables disponibles para evaluación o en evaluación y los "
                        + "filtra por proyecto de grado, asesor y estado, entre otros campos. Cada elemento trae la versión, "
                        + "el proyecto, el asesor y el estado";
        public static final String CONSULTAR_JURADO_RESP_200 = "Página de entregables de proyecto de grado por evaluar";
        public static final String CONSULTAR_JURADO_RESP_400 =
                "Un campo de filtro u orden no es válido, un operador no aplica al campo, un identificador no es un UUID "
                        + "o el tamaño de página está fuera de rango";
        public static final String CONSULTAR_ESTUDIANTE_SUMMARY = "Consultar los entregables de un proyecto de grado";
        public static final String CONSULTAR_ESTUDIANTE_DESCRIPTION =
                "El estudiante lista, de forma paginada, los entregables de un proyecto de grado al que pertenece y los "
                        + "filtra por asesor y estado, entre otros campos. Cada elemento trae la versión, el proyecto, "
                        + "el asesor y el estado";
        public static final String CONSULTAR_ESTUDIANTE_RESP_200 = "Página de entregables del proyecto de grado";
        public static final String CONSULTAR_ESTUDIANTE_RESP_400 =
                "Un campo de filtro u orden no es válido, un operador no aplica al campo, un identificador no es un UUID "
                        + "o el tamaño de página está fuera de rango";
        public static final String CONSULTAR_ESTUDIANTE_RESP_422 = "El estudiante no pertenece al proyecto de grado";
    }

    public static final class ArtefactoEntregable {

        private ArtefactoEntregable() {}

        public static final String TAG_NAME = "Artefactos de entregable";
        public static final String TAG_DESCRIPTION = "Consulta de los artefactos vinculados a un entregable de proyecto de grado";
        public static final String CONSULTAR_SUMMARY = "Consultar los artefactos de un entregable";
        public static final String CONSULTAR_DESCRIPTION =
                "El asesor consulta los artefactos vinculados a un entregable de proyecto de grado suyo. "
                        + "Cada elemento trae el documento, el repositorio y la URI del archivo";
        public static final String CONSULTAR_RESP_200 = "Artefactos del entregable; lista vacía si no tiene ninguno";
        public static final String CONSULTAR_RESP_400 = "El identificador del entregable no es un UUID válido";
        public static final String CONSULTAR_RESP_422 = "El entregable no existe o no es del asesor autenticado";
    }
}
