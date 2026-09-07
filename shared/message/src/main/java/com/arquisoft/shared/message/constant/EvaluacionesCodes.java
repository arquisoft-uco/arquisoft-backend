package com.arquisoft.shared.message.constant;

public final class EvaluacionesCodes {

    private EvaluacionesCodes() {}

    public static final class ItemCualitativoJurado {

        private ItemCualitativoJurado() {}

        public static final String NOMBRE_REQUERIDO = "ITEM_CUALITATIVO_JURADO_NOMBRE_REQUERIDO";
        public static final String NOMBRE_DEMASIADO_LARGO =
                "ITEM_CUALITATIVO_JURADO_NOMBRE_DEMASIADO_LARGO";
        public static final String DESCRIPCION_REQUERIDA =
                "ITEM_CUALITATIVO_JURADO_DESCRIPCION_REQUERIDA";
        public static final String DESCRIPCION_DEMASIADO_LARGA =
                "ITEM_CUALITATIVO_JURADO_DESCRIPCION_DEMASIADO_LARGA";
        public static final String NOMBRE_DUPLICADO =
                "ITEM_CUALITATIVO_JURADO_NOMBRE_DUPLICADO";
        public static final String ITEM_ID_REQUERIDO =
                "ITEM_CUALITATIVO_JURADO_ID_REQUERIDO";
        public static final String ITEM_NO_ENCONTRADO =
                "ITEM_CUALITATIVO_JURADO_NO_ENCONTRADO";
    }

    public static final class CriterioItemCualitativoJurado {

        private CriterioItemCualitativoJurado() {}

        public static final String NOMBRE_REQUERIDO =
                "CRITERIO_ITEM_CUALITATIVO_JURADO_NOMBRE_REQUERIDO";
        public static final String NOMBRE_DEMASIADO_LARGO =
                "CRITERIO_ITEM_CUALITATIVO_JURADO_NOMBRE_DEMASIADO_LARGO";
        public static final String DESCRIPCION_REQUERIDA =
                "CRITERIO_ITEM_CUALITATIVO_JURADO_DESCRIPCION_REQUERIDA";
        public static final String DESCRIPCION_DEMASIADO_LARGA =
                "CRITERIO_ITEM_CUALITATIVO_JURADO_DESCRIPCION_DEMASIADO_LARGA";
    }

    public static final class EvaluacionCualitativaJurado {

        private EvaluacionCualitativaJurado() {}

        public static final String EVALUACION_JURADO_REQUERIDO =
                "EVALUACION_CUALITATIVA_JURADO_EVALUACION_JURADO_REQUERIDO";
        public static final String ESTUDIANTE_REQUERIDO =
                "EVALUACION_CUALITATIVA_JURADO_ESTUDIANTE_REQUERIDO";
        public static final String ITEM_REQUERIDO =
                "EVALUACION_CUALITATIVA_JURADO_ITEM_REQUERIDO";
        public static final String CRITERIO_REQUERIDO =
                "EVALUACION_CUALITATIVA_JURADO_CRITERIO_REQUERIDO";
        public static final String EVALUACION_JURADO_NO_ENCONTRADA =
                "EVALUACION_CUALITATIVA_JURADO_EVALUACION_JURADO_NO_ENCONTRADA";
        public static final String EVALUACION_JURADO_NO_PERTENECE =
                "EVALUACION_CUALITATIVA_JURADO_EVALUACION_JURADO_NO_PERTENECE";
    }

    public static final class EntregableProyectoAcceso {

        private EntregableProyectoAcceso() {}

        public static final String ENTREGABLE_REQUERIDO = "ENTREGABLE_PROYECTO_ACCESO_ENTREGABLE_REQUERIDO";
        public static final String PROYECTO_REQUERIDO = "ENTREGABLE_PROYECTO_ACCESO_PROYECTO_REQUERIDO";
        public static final String VERSION_ENTREGABLE_INVALIDA =
                "ENTREGABLE_PROYECTO_ACCESO_VERSION_ENTREGABLE_INVALIDA";
        public static final String OCURRIDO_EN_REQUERIDO = "ENTREGABLE_PROYECTO_ACCESO_OCURRIDO_EN_REQUERIDO";
    }

    public static final class ProyectoEstudianteAcceso {

        private ProyectoEstudianteAcceso() {}

        public static final String PROYECTO_REQUERIDO = "PROYECTO_ESTUDIANTE_ACCESO_PROYECTO_REQUERIDO";
        public static final String ESTUDIANTE_REQUERIDO = "PROYECTO_ESTUDIANTE_ACCESO_ESTUDIANTE_REQUERIDO";
        public static final String OCURRIDO_EN_REQUERIDO = "PROYECTO_ESTUDIANTE_ACCESO_OCURRIDO_EN_REQUERIDO";
    }
}
