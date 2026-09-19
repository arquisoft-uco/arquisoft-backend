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
        public static final String ITEM_REQUERIDO =
                "EVALUACION_CUALITATIVA_JURADO_ITEM_REQUERIDO";
        public static final String CRITERIO_REQUERIDO =
                "EVALUACION_CUALITATIVA_JURADO_CRITERIO_REQUERIDO";
        public static final String EVALUACION_JURADO_NO_ENCONTRADA =
                "EVALUACION_CUALITATIVA_JURADO_EVALUACION_JURADO_NO_ENCONTRADA";
        public static final String ITEM_INVALIDO =
                "EVALUACION_CUALITATIVA_JURADO_ITEM_INVALIDO";
        public static final String CRITERIO_INVALIDO =
                "EVALUACION_CUALITATIVA_JURADO_CRITERIO_INVALIDO";
        public static final String ITEMS_NO_ENCONTRADOS =
                "EVALUACION_CUALITATIVA_JURADO_ITEMS_NO_ENCONTRADOS";
        public static final String CRITERIOS_NO_ENCONTRADOS =
                "EVALUACION_CUALITATIVA_JURADO_CRITERIOS_NO_ENCONTRADOS";
        public static final String ITEMS_YA_REGISTRADOS =
                "EVALUACION_CUALITATIVA_JURADO_ITEMS_YA_REGISTRADOS";
    }

    public static final class RegistroEvaluacionesCualitativasJurado {

        private RegistroEvaluacionesCualitativasJurado() {}

        public static final String LOTE_REQUERIDO = "REGISTRO_EVALUACIONES_CUALITATIVAS_LOTE_REQUERIDO";
        public static final String LOTE_VACIO = "REGISTRO_EVALUACIONES_CUALITATIVAS_LOTE_VACIO";
        public static final String PAR_REQUERIDO = "REGISTRO_EVALUACIONES_CUALITATIVAS_PAR_REQUERIDO";
        public static final String ITEMS_REPETIDOS = "REGISTRO_EVALUACIONES_CUALITATIVAS_ITEMS_REPETIDOS";
        public static final String PADRES_DISTINTOS = "REGISTRO_EVALUACIONES_CUALITATIVAS_PADRES_DISTINTOS";
    }

    public static final class Evaluacion {

        private Evaluacion() {}

        public static final String EVALUACION_REQUERIDO = "EVALUACION_EVALUACION_REQUERIDO";
        public static final String ESTADO_REQUERIDO = "EVALUACION_ESTADO_REQUERIDO";
        public static final String ESTADO_FINALIZADA = "EVALUACION_ESTADO_FINALIZADA";
        public static final String ESTADO_NO_ENCONTRADO = "EVALUACION_ESTADO_NO_ENCONTRADO";
    }

    public static final class ItemCuantitativoJurado {

        private ItemCuantitativoJurado() {}

        public static final String NOMBRE_REQUERIDO =
                "ITEM_CUANTITATIVO_JURADO_NOMBRE_REQUERIDO";
        public static final String NOMBRE_DEMASIADO_LARGO =
                "ITEM_CUANTITATIVO_JURADO_NOMBRE_DEMASIADO_LARGO";
        public static final String DESCRIPCION_REQUERIDA =
                "ITEM_CUANTITATIVO_JURADO_DESCRIPCION_REQUERIDA";
        public static final String DESCRIPCION_DEMASIADO_LARGA =
                "ITEM_CUANTITATIVO_JURADO_DESCRIPCION_DEMASIADO_LARGA";
        public static final String CATEGORIA_REQUERIDA =
                "ITEM_CUANTITATIVO_JURADO_CATEGORIA_REQUERIDA";
        public static final String VALOR_REQUERIDO =
                "ITEM_CUANTITATIVO_JURADO_VALOR_REQUERIDO";
        public static final String VALOR_FUERA_DE_RANGO =
                "ITEM_CUANTITATIVO_JURADO_VALOR_FUERA_DE_RANGO";
        public static final String CATEGORIA_NO_ENCONTRADA =
                "ITEM_CUANTITATIVO_JURADO_CATEGORIA_NO_ENCONTRADA";
        public static final String NOMBRE_CATEGORIA_DUPLICADO =
                "ITEM_CUANTITATIVO_JURADO_NOMBRE_CATEGORIA_DUPLICADO";
        public static final String ITEM_ID_REQUERIDO =
                "ITEM_CUANTITATIVO_JURADO_ID_REQUERIDO";
        public static final String ITEM_NO_ENCONTRADO =
                "ITEM_CUANTITATIVO_JURADO_NO_ENCONTRADO";
    }
}
