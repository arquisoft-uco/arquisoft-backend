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
        public static final String CONSULTAR_SUMMARY = "Consultar ítems cualitativos";
        public static final String CONSULTAR_DESCRIPTION = "Consulta todos los ítems cualitativos disponibles para el jurado, ordenados por nombre";
        public static final String CONSULTAR_RESP_200 = "Listado de ítems cualitativos del jurado";
        public static final String MODIFICAR_SUMMARY = "Modificar descripción del ítem cualitativo";
        public static final String MODIFICAR_DESCRIPTION =
                "Modifica la descripción de un ítem cualitativo existente del jurado";
        public static final String MODIFICAR_RESP_204 = "Descripción actualizada";
        public static final String MODIFICAR_RESP_400 = "Datos de entrada inválidos";
        public static final String MODIFICAR_RESP_422 = "El ítem cualitativo no existe";
    }

    public static final class CriterioItemCualitativoJurado {

        private CriterioItemCualitativoJurado() {}

        public static final String TAG_NAME = "Criterios de ítems cualitativos del jurado";
        public static final String TAG_DESCRIPTION =
                "Consulta de los criterios usados por el jurado para calificar ítems cualitativos";
        public static final String CONSULTAR_SUMMARY = "Consultar criterios cualitativos";
        public static final String CONSULTAR_DESCRIPTION =
                "Consulta todos los criterios disponibles para calificar ítems cualitativos del jurado, "
                        + "ordenados por nombre";
        public static final String CONSULTAR_RESP_200 = "Listado de criterios cualitativos del jurado";
    }

    public static final class EvaluacionCualitativaJurado {

        private EvaluacionCualitativaJurado() {}

        public static final String TAG_NAME = "Evaluaciones cualitativas del jurado";
        public static final String TAG_DESCRIPTION =
                "Consulta de las evaluaciones cualitativas asociadas a una evaluación de jurado";
        public static final String CONSULTAR_SUMMARY = "Consultar evaluaciones cualitativas del jurado";
        public static final String CONSULTAR_DESCRIPTION =
                "Consulta todas las evaluaciones cualitativas de una evaluación de jurado a la que el "
                        + "estudiante autenticado está vinculado";
        public static final String CONSULTAR_RESP_200 = "Listado de evaluaciones cualitativas del jurado";
        public static final String CONSULTAR_RESP_400 = "Datos de entrada inválidos";
        public static final String CONSULTAR_RESP_422 =
                "La evaluación de jurado no existe o no pertenece al estudiante autenticado";
    }
}
