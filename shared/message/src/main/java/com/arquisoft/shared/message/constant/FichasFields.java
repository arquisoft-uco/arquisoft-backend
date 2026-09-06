package com.arquisoft.shared.message.constant;

/**
 * Nombres de campo del contexto fichas.
 *
 * <p>Identifican el campo dentro de {@code fieldErrors[]} y dentro de los agregados al acumular
 * errores de validación. Son identificadores del contrato — el cliente los usa para asociar un
 * error a un campo del formulario — y además se referencian desde anotaciones, así que se quedan
 * como constantes en lugar de salir al bundle.
 */
public final class FichasFields {

    private FichasFields() {}

    public static final class FichaPerfil {

        private FichaPerfil() {}

        public static final String ID = "fichaPerfilId";
        public static final String TITULO = "tituloProyecto";
        public static final String ASESOR_FICHA = "asesorFicha";
        public static final String ESTUDIANTE = "estudiante";
        public static final String ESTUDIANTES = "estudiantes";
        public static final String ESTADO_FICHA = "estadoFicha";
    }

    public static final class ItemFichaPerfil {

        private ItemFichaPerfil() {}

        public static final String ITEM = "item";
        public static final String FICHA_PERFIL = "fichaPerfil";
        public static final String ASESOR_FICHA = "asesorFicha";
        public static final String TIPO_ITEM = "tipoItem";
        public static final String CONTENIDO = "contenido";
        public static final String ESTUDIANTE = "estudiante";
        public static final String REPRESENTANTE_COMITE = "representanteComite";
        public static final String ESTADO_FICHA = "estadoFicha";
        public static final String REVISIONES = "revisiones";
    }

    public static final class RevisionItem {

        private RevisionItem() {}

        public static final String ITEM = "item";
        public static final String ESTADO_REVISION = "estadoRevision";
        public static final String REVISION_ITEM = "revisionItem";
        public static final String ASESOR_FICHA = "asesorFicha";
    }

    public static final class EstudianteFichaPerfil {

        private EstudianteFichaPerfil() {}

        public static final String FICHA_PERFIL = "fichaPerfil";
        public static final String ESTUDIANTE = "estudiante";
        public static final String ESTUDIANTES = "estudiantes";
    }

    public static final class EstadoFichaPerfil {

        private EstadoFichaPerfil() {}

        public static final String FICHA_PERFIL = "fichaPerfil";
        public static final String ESTADO_FICHA = "estadoFicha";
    }

    public static final class EvaluacionFichaPerfil {

        private EvaluacionFichaPerfil() {}

        public static final String REPRESENTANTE_COMITE = "representanteComite";
        public static final String FICHA_PERFIL = "fichaPerfil";
    }

    public static final class EstadoEvaluacionFicha {

        private EstadoEvaluacionFicha() {}

        public static final String EVALUACION_FICHA_PERFIL = "evaluacionFichaPerfil";
        public static final String ESTADO_EVALUACION = "estadoEvaluacion";
        public static final String REPRESENTANTE_COMITE = "representanteComite";
    }
}
