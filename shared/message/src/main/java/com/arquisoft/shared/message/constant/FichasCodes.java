package com.arquisoft.shared.message.constant;

/**
 * Códigos de error del contexto fichas.
 *
 * <p>Contrato de la API, no texto: viajan en {@code ErrorResponseDTO.errorCode}. Ver la nota de
 * {@link AppCodes} sobre por qué no salen al bundle.
 */
public final class FichasCodes {

    private FichasCodes() {}

    public static final class FichaPerfil {

        private FichaPerfil() {}

        public static final String ID_REQUERIDO = "FICHA_ID_REQUERIDO";
        public static final String TITULO_REQUERIDO = "FICHA_TITULO_REQUERIDO";
        public static final String TITULO_DEMASIADO_LARGO = "FICHA_TITULO_DEMASIADO_LARGO";
        public static final String ASESOR_REQUERIDO = "FICHA_ASESOR_REQUERIDO";
        public static final String ESTUDIANTE_REQUERIDO = "FICHA_ESTUDIANTE_REQUERIDO";
        public static final String FICHA_TITULO_DUPLICADO = "FICHA_TITULO_DUPLICADO";
        public static final String ASESOR_NO_ENCONTRADO = "ASESOR_NO_ENCONTRADO";
        public static final String FICHA_NO_ENCONTRADA = "FICHA_NO_ENCONTRADA";
        public static final String FICHA_NO_PROPIETARIO = "FICHA_NO_PROPIETARIO";
        public static final String FICHA_NO_PERTENECE_ASESOR = "FICHA_NO_PERTENECE_ASESOR";
        public static final String MISMO_ASESOR = "MISMO_ASESOR";
        public static final String ESTADO_TERMINAL = "ESTADO_TERMINAL";
    }

    public static final class ItemFichaPerfil {

        private ItemFichaPerfil() {}

        public static final String ITEM_ID_REQUERIDO = "ITEM_ID_REQUERIDO";
        public static final String ESTUDIANTE_REQUERIDO = "ITEM_ESTUDIANTE_REQUERIDO";
        public static final String FICHA_PERFIL_ID_REQUERIDO = "ITEM_FICHA_PERFIL_ID_REQUERIDO";
        public static final String TIPO_ITEM_REQUERIDO = "ITEM_TIPO_ITEM_REQUERIDO";
        public static final String CONTENIDO_REQUERIDO = "ITEM_CONTENIDO_REQUERIDO";
        public static final String CONTENIDO_DEMASIADO_LARGO = "ITEM_CONTENIDO_DEMASIADO_LARGO";
        public static final String ITEM_TIPO_DUPLICADO = "ITEM_TIPO_DUPLICADO";
        public static final String ITEM_FICHA_NO_AUTORIZADA = "ITEM_FICHA_NO_AUTORIZADA";
        public static final String TIPO_ITEM_INVALIDO = "TIPO_ITEM_INVALIDO";
        public static final String ITEM_NO_ENCONTRADO = "ITEM_NO_ENCONTRADO";
        public static final String ESTADO_FICHA_REQUERIDO = "ITEM_ESTADO_FICHA_REQUERIDO";
        public static final String ESTADO_FICHA_NO_MODIFICABLE = "ESTADO_FICHA_NO_MODIFICABLE";
        public static final String ITEM_CON_REVISIONES = "ITEM_CON_REVISIONES";
    }

    public static final class RevisionItem {

        private RevisionItem() {}

        public static final String ITEM_REQUERIDO = "REVISION_ITEM_ITEM_REQUERIDO";
        public static final String ASESOR_FICHA_REQUERIDO = "REVISION_ITEM_ASESOR_FICHA_REQUERIDO";
        public static final String ESTADO_REVISION_REQUERIDO = "REVISION_ITEM_ESTADO_REVISION_REQUERIDO";
        public static final String ESTADO_REVISION_DEMASIADO_LARGO = "REVISION_ITEM_ESTADO_REVISION_DEMASIADO_LARGO";
        public static final String ESTADO_REVISION_NO_ENCONTRADO = "REVISION_ITEM_ESTADO_REVISION_NO_ENCONTRADO";
        public static final String YA_EXISTE = "REVISION_ITEM_YA_EXISTE";
        public static final String REVISION_ITEM_REQUERIDO = "REVISION_ITEM_REVISION_ITEM_REQUERIDO";
        public static final String NO_ENCONTRADA = "REVISION_ITEM_NO_ENCONTRADA";
    }

    public static final class Estudiante {

        private Estudiante() {}

        public static final String ESTUDIANTE_NO_ENCONTRADO = "ESTUDIANTE_NO_ENCONTRADO";
    }

    public static final class EstudianteFichaPerfil {

        private EstudianteFichaPerfil() {}

        public static final String FICHA_PERFIL_ID_REQUERIDO = "ESTUDIANTE_FICHA_PERFIL_FICHA_ID_REQUERIDO";
        public static final String ESTUDIANTE_ID_REQUERIDO = "ESTUDIANTE_FICHA_PERFIL_ESTUDIANTE_ID_REQUERIDO";
        public static final String ESTUDIANTES_REQUERIDOS = "ESTUDIANTE_FICHA_PERFIL_ESTUDIANTES_REQUERIDOS";
        public static final String ESTUDIANTE_DUPLICADO = "ESTUDIANTE_DUPLICADO";
        public static final String LIMITE_ESTUDIANTES_EXCEDIDO = "LIMITE_ESTUDIANTES_EXCEDIDO";
        public static final String ESTUDIANTE_FICHA_PERFIL_NO_ENCONTRADO = "ESTUDIANTE_FICHA_PERFIL_NO_ENCONTRADO";
    }

    public static final class EstadoFichaPerfil {

        private EstadoFichaPerfil() {}

        public static final String FICHA_PERFIL_ID_REQUERIDO = "ESTADO_FICHA_PERFIL_FICHA_PERFIL_ID_REQUERIDO";
        public static final String ESTADO_FICHA_REQUERIDO = "ESTADO_FICHA_PERFIL_ESTADO_FICHA_REQUERIDO";
        public static final String NO_ENCONTRADO = "ESTADO_FICHA_PERFIL_NO_ENCONTRADO";
        public static final String ESTADO_TERMINAL = "ESTADO_FICHA_PERFIL_ESTADO_TERMINAL";
    }

    public static final class EstadoFicha {

        private EstadoFicha() {}

        public static final String NO_ENCONTRADO = "ESTADO_FICHA_NO_ENCONTRADO";
    }

    public static final class EvaluacionFichaPerfil {

        private EvaluacionFichaPerfil() {}

        public static final String EVALUACION_DUPLICADA = "EVALUACION_DUPLICADA";
        public static final String REPRESENTANTE_REQUERIDO = "REPRESENTANTE_REQUERIDO";
        public static final String FICHA_REQUERIDA = "FICHA_REQUERIDA";
    }

    public static final class RepresentanteComite {

        private RepresentanteComite() {}

        public static final String REPRESENTANTE_NO_ENCONTRADO = "REPRESENTANTE_NO_ENCONTRADO";
    }

    public static final class EstadoEvaluacionFicha {

        private EstadoEvaluacionFicha() {}

        public static final String EVALUACION_NO_ENCONTRADA = "EVALUACION_NO_ENCONTRADA";
        public static final String ESTADO_NO_ENCONTRADO = "ESTADO_NO_ENCONTRADO";
        public static final String ESTADO_DUPLICADO = "ESTADO_DUPLICADO";
        public static final String TRANSICION_INVALIDA = "TRANSICION_INVALIDA";
        public static final String EVALUACION_REQUERIDA = "EVALUACION_REQUERIDA";
        public static final String ESTADO_REQUERIDO = "ESTADO_REQUERIDO";
        public static final String ESTADO_EN_EVALUACION_NO_MANUAL = "ESTADO_EN_EVALUACION_NO_MANUAL";
        public static final String EVALUACION_NO_PROPIA = "EVALUACION_NO_PROPIA";
        public static final String REPRESENTANTE_REQUERIDO = "ESTADO_EVALUACION_REPRESENTANTE_REQUERIDO";
    }
}
