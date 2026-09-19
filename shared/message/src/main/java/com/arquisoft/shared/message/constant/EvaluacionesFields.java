package com.arquisoft.shared.message.constant;

public final class EvaluacionesFields {

    private EvaluacionesFields() {}

    public static final class ItemCualitativoJurado {

        private ItemCualitativoJurado() {}

        public static final String NOMBRE = "nombre";
        public static final String DESCRIPCION = "descripcion";
        public static final String ITEM = "itemCualitativoJurado";
    }

    public static final class CriterioItemCualitativoJurado {

        private CriterioItemCualitativoJurado() {}

        public static final String NOMBRE = "nombre";
        public static final String DESCRIPCION = "descripcion";
    }

    public static final class EvaluacionCualitativaJurado {

        private EvaluacionCualitativaJurado() {}

        public static final String EVALUACION_JURADO = "evaluacionJurado";
        public static final String ITEM = "item";
        public static final String CRITERIO = "criterio";
    }

    public static final class RegistroEvaluacionesCualitativasJurado {

        private RegistroEvaluacionesCualitativasJurado() {}

        public static final String EVALUACIONES = "evaluaciones";
    }

    public static final class Evaluacion {

        private Evaluacion() {}

        public static final String EVALUACION = "evaluacion";
        public static final String ESTADO = "estado";
    }

    public static final class ItemCuantitativoJurado {

        private ItemCuantitativoJurado() {}

        public static final String NOMBRE = "nombre";
        public static final String DESCRIPCION = "descripcion";
        public static final String CATEGORIA = "categoria";
        public static final String VALOR = "valor";
    }
}
