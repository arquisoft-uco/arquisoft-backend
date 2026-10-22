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

    public static final class CategoriaItemCuantitativoAsesor {

        private CategoriaItemCuantitativoAsesor() {}

        public static final String NOMBRE = "nombre";
    }

    public static final class CategoriaItemCuantitativoJurado {

        private CategoriaItemCuantitativoJurado() {}

        public static final String NOMBRE = "nombre";
    }

    public static final class EvaluacionCualitativaJurado {

        private EvaluacionCualitativaJurado() {}

        public static final String EVALUACION_JURADO = "evaluacionJurado";
        public static final String ITEM = "item";
        public static final String CRITERIO = "criterio";
    }

    public static final class EvaluacionCualitativaAsesor {

        private EvaluacionCualitativaAsesor() {}

        public static final String EVALUACION_ASESOR = "evaluacionAsesor";
        public static final String ASESOR = "asesor";
        public static final String ITEM = "item";
        public static final String CRITERIO = "criterio";
    }

    public static final class RegistroEvaluacionesCualitativasAsesor {

        private RegistroEvaluacionesCualitativasAsesor() {}

        public static final String EVALUACIONES = "evaluaciones";
    }

    public static final class EvaluacionCuantitativaJurado {

        private EvaluacionCuantitativaJurado() {}

        public static final String ID = "id";
        public static final String EVALUACION_JURADO = "evaluacionJurado";
        public static final String JURADO = "jurado";
        public static final String ITEM = "item";
        public static final String PUNTAJE = "puntaje";
    }

    public static final class RegistroEvaluacionesCualitativasJurado {

        private RegistroEvaluacionesCualitativasJurado() {}

        public static final String EVALUACIONES = "evaluaciones";
    }

    public static final class OmisionEvaluacionesCualitativasJurado {

        private OmisionEvaluacionesCualitativasJurado() {}

        public static final String EVALUACIONES = "evaluaciones";
    }

    public static final class OmisionEvaluacionesCuantitativasJurado {

        private OmisionEvaluacionesCuantitativasJurado() {}

        public static final String EVALUACIONES = "evaluaciones";
    }

    public static final class Evaluacion {

        private Evaluacion() {}

        public static final String EVALUACION = "evaluacion";
        public static final String ESTADO = "estado";
        public static final String ASESOR = "asesor";
    }

    public static final class EvaluacionJurado {

        private EvaluacionJurado() {}

        public static final String EVALUACION = "evaluacion";
        public static final String JURADO = "jurado";
    }

    public static final class AsignacionJuradosEvaluacion {

        private AsignacionJuradosEvaluacion() {}

        public static final String JURADOS = "jurados";
    }

    public static final class EstudianteProyectoGrado {

        private EstudianteProyectoGrado() {}

        public static final String PROYECTO_GRADO = "proyectoGrado";
        public static final String ESTUDIANTES = "estudiantes";
        public static final String ESTUDIANTE = "estudiante";
        public static final String OCURRIDO_EN = "ocurridoEn";
    }

    public static final class Jurado {

        private Jurado() {}

        public static final String ID = "id";
        public static final String IDENTIFICADOR = "identificador";
        public static final String NOMBRE = "nombre";
        public static final String EMAIL = "email";
        public static final String OCURRIDO_EN = "ocurridoEn";
    }

    public static final class ItemCuantitativoJurado {

        private ItemCuantitativoJurado() {}

        public static final String NOMBRE = "nombre";
        public static final String DESCRIPCION = "descripcion";
        public static final String CATEGORIA = "categoria";
        public static final String VALOR = "valor";
        public static final String ITEM = "itemCuantitativoJurado";
    }

    public static final class ObservacionItemJurado {

        private ObservacionItemJurado() {}

        public static final String ID = "id";
        public static final String EVALUACION_CUANTITATIVA_JURADO = "evaluacionCuantitativaJurado";
        public static final String DESCRIPCION = "descripcion";
    }
}
