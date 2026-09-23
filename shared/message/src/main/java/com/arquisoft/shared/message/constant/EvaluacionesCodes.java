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
        public static final String ITEM_EN_USO =
                "ITEM_CUALITATIVO_JURADO_EN_USO";
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

    public static final class CategoriaItemCuantitativoJurado {

        private CategoriaItemCuantitativoJurado() {}

        public static final String NOMBRE_REQUERIDO =
                "CATEGORIA_ITEM_CUANTITATIVO_JURADO_NOMBRE_REQUERIDO";
        public static final String NOMBRE_DEMASIADO_LARGO =
                "CATEGORIA_ITEM_CUANTITATIVO_JURADO_NOMBRE_DEMASIADO_LARGO";
        public static final String DESCRIPCION_REQUERIDA =
                "CATEGORIA_ITEM_CUANTITATIVO_JURADO_DESCRIPCION_REQUERIDA";
        public static final String DESCRIPCION_DEMASIADO_LARGA =
                "CATEGORIA_ITEM_CUANTITATIVO_JURADO_DESCRIPCION_DEMASIADO_LARGA";
        public static final String NOMBRE_FILTRO_DEMASIADO_LARGO =
                "CATEGORIA_ITEM_CUANTITATIVO_JURADO_NOMBRE_FILTRO_DEMASIADO_LARGO";
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
        public static final String NO_ENCONTRADA = "EVALUACION_NO_ENCONTRADA";
    }

    public static final class EvaluacionJurado {

        private EvaluacionJurado() {}

        public static final String EVALUACION_REQUERIDO = "EVALUACION_JURADO_EVALUACION_REQUERIDO";
        public static final String JURADO_REQUERIDO = "EVALUACION_JURADO_JURADO_REQUERIDO";
    }

    public static final class EvaluacionCuantitativaJurado {

        private EvaluacionCuantitativaJurado() {}

        public static final String ID_REQUERIDO =
                "EVALUACION_CUANTITATIVA_JURADO_ID_REQUERIDO";
        public static final String EVALUACION_JURADO_REQUERIDO =
                "EVALUACION_CUANTITATIVA_JURADO_EVALUACION_JURADO_REQUERIDO";
        public static final String JURADO_REQUERIDO =
                "EVALUACION_CUANTITATIVA_JURADO_JURADO_REQUERIDO";
        public static final String ITEM_REQUERIDO =
                "EVALUACION_CUANTITATIVA_JURADO_ITEM_REQUERIDO";
        public static final String PUNTAJE_REQUERIDO =
                "EVALUACION_CUANTITATIVA_JURADO_PUNTAJE_REQUERIDO";
        public static final String PUNTAJE_FUERA_DE_RANGO =
                "EVALUACION_CUANTITATIVA_JURADO_PUNTAJE_FUERA_DE_RANGO";
        public static final String NO_ENCONTRADA =
                "EVALUACION_CUANTITATIVA_JURADO_NO_ENCONTRADA";
        public static final String NO_PERTENECE_JURADO =
                "EVALUACION_CUANTITATIVA_JURADO_NO_PERTENECE_JURADO";
        public static final String EVALUACION_FINALIZADA =
                "EVALUACION_CUANTITATIVA_JURADO_EVALUACION_FINALIZADA";
        public static final String PUNTAJE_EXCEDE_VALOR_ITEM =
                "EVALUACION_CUANTITATIVA_JURADO_PUNTAJE_EXCEDE_VALOR_ITEM";
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
        public static final String ITEM_EN_USO =
                "ITEM_CUANTITATIVO_JURADO_EN_USO";
    }

    public static final class ObservacionItemJurado {

        private ObservacionItemJurado() {}

        public static final String EVALUACION_CUANTITATIVA_JURADO_REQUERIDA =
                "OBSERVACION_ITEM_JURADO_EVALUACION_CUANTITATIVA_JURADO_REQUERIDA";
        public static final String DESCRIPCION_REQUERIDA =
                "OBSERVACION_ITEM_JURADO_DESCRIPCION_REQUERIDA";
        public static final String DESCRIPCION_DEMASIADO_LARGA =
                "OBSERVACION_ITEM_JURADO_DESCRIPCION_DEMASIADO_LARGA";
        public static final String EVALUACION_NO_ENCONTRADA =
                "OBSERVACION_ITEM_JURADO_EVALUACION_NO_ENCONTRADA";
        public static final String EVALUACION_JURADO_FINALIZADA =
                "OBSERVACION_ITEM_JURADO_EVALUACION_JURADO_FINALIZADA";
        public static final String DESCRIPCION_DUPLICADA =
                "OBSERVACION_ITEM_JURADO_DESCRIPCION_DUPLICADA";
        public static final String ID_REQUERIDO =
                "OBSERVACION_ITEM_JURADO_ID_REQUERIDO";
        public static final String NO_ENCONTRADA =
                "OBSERVACION_ITEM_JURADO_NO_ENCONTRADA";
    }
}
