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
        public static final String ESTUDIANTE = "estudiante";
        public static final String ITEM = "item";
        public static final String CRITERIO = "criterio";
    }

    public static final class EntregableProyectoAcceso {

        private EntregableProyectoAcceso() {}

        public static final String ENTREGABLE = "entregable";
        public static final String PROYECTO = "proyecto";
        public static final String VERSION_ENTREGABLE = "versionEntregable";
        public static final String OCURRIDO_EN = "ocurridoEn";
    }

    public static final class ProyectoEstudianteAcceso {

        private ProyectoEstudianteAcceso() {}

        public static final String PROYECTO = "proyecto";
        public static final String ESTUDIANTE = "estudiante";
        public static final String OCURRIDO_EN = "ocurridoEn";
    }
}
