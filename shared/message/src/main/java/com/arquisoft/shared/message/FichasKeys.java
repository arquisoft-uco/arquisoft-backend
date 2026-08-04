package com.arquisoft.shared.message;

/**
 * Claves del catálogo para el contexto fichas — bundle {@code messages/fichas.properties}.
 *
 * <p>Las claves {@code ERROR_*} alimentan {@link MessageCatalog#formatear(String, Object...)};
 * las {@code LOG_*} se recuperan con {@link MessageCatalog#obtener(String)} y conservan los
 * marcadores {@code {}} que sustituye SLF4J en la capa de logging.
 */
public final class FichasKeys {

    private FichasKeys() {}

    public static final class FichaPerfil {

        private FichaPerfil() {}

        public static final String ERROR_TITULO_DUPLICADO = "fichas.dominio.fichaperfil.error.titulo-duplicado";
        public static final String ERROR_ASESOR_NO_ENCONTRADO = "fichas.dominio.fichaperfil.error.asesor-no-encontrado";
        public static final String ERROR_NO_ENCONTRADA = "fichas.dominio.fichaperfil.error.no-encontrada";
        public static final String ERROR_NO_PROPIETARIO = "fichas.dominio.fichaperfil.error.no-propietario";
        public static final String ERROR_MISMO_ASESOR = "fichas.dominio.fichaperfil.error.mismo-asesor";
        public static final String ERROR_ESTADO_TERMINAL = "fichas.dominio.fichaperfil.error.estado-terminal";

        public static final String LOG_REGISTRADA = "fichas.aplicacion.fichaperfil.log.registrada";
        public static final String LOG_MODIFICADA = "fichas.aplicacion.fichaperfil.log.modificada";
        public static final String LOG_ASESOR_CAMBIADO = "fichas.aplicacion.fichaperfil.log.asesor-cambiado";
        public static final String LOG_CONSULTANDO = "fichas.aplicacion.fichaperfil.log.consultando";
        public static final String LOG_CONSULTA_COMPLETADA = "fichas.aplicacion.fichaperfil.log.consulta-completada";
        public static final String LOG_GUARDADA = "fichas.infraestructura.fichaperfil.log.guardada";
        public static final String LOG_ORDENAMIENTO_INVALIDO = "fichas.infraestructura.fichaperfil.log.ordenamiento-invalido";
        public static final String LOG_USO_INVALIDO_API_ORDEN = "fichas.infraestructura.fichaperfil.log.uso-invalido-api-orden";
    }

    public static final class ItemFichaPerfil {

        private ItemFichaPerfil() {}

        public static final String ERROR_TIPO_INVALIDO = "fichas.dominio.itemfichaperfil.error.tipo-invalido";
        public static final String ERROR_TIPO_DUPLICADO = "fichas.dominio.itemfichaperfil.error.tipo-duplicado";
        public static final String ERROR_FICHA_NO_AUTORIZADA = "fichas.dominio.itemfichaperfil.error.ficha-no-autorizada";
        public static final String ERROR_NO_ENCONTRADO = "fichas.dominio.itemfichaperfil.error.no-encontrado";
        public static final String ERROR_ESTADO_FICHA_NO_MODIFICABLE = "fichas.dominio.itemfichaperfil.error.estado-ficha-no-modificable";
        public static final String ERROR_CON_REVISIONES = "fichas.dominio.itemfichaperfil.error.con-revisiones";

        public static final String LOG_AGREGADO = "fichas.aplicacion.itemfichaperfil.log.agregado";
        public static final String LOG_MODIFICADO = "fichas.aplicacion.itemfichaperfil.log.modificado";
        public static final String LOG_REMOVIDO = "fichas.aplicacion.itemfichaperfil.log.removido";
    }

    public static final class Estudiante {

        private Estudiante() {}

        public static final String ERROR_NO_ENCONTRADO = "fichas.dominio.estudiante.error.no-encontrado";
    }

    public static final class EstudianteFichaPerfil {

        private EstudianteFichaPerfil() {}

        public static final String ERROR_DUPLICADO = "fichas.dominio.estudiantefichaperfil.error.duplicado";
        public static final String ERROR_LIMITE_EXCEDIDO = "fichas.dominio.estudiantefichaperfil.error.limite-excedido";
        public static final String ERROR_RELACION_NO_ENCONTRADA = "fichas.dominio.estudiantefichaperfil.error.relacion-no-encontrada";

        public static final String LOG_ASIGNADO = "fichas.aplicacion.estudiantefichaperfil.log.asignado";
        public static final String LOG_REMOVIDO = "fichas.aplicacion.estudiantefichaperfil.log.removido";
    }

    public static final class EstadoFichaPerfil {

        private EstadoFichaPerfil() {}

        public static final String ERROR_NO_ENCONTRADO = "fichas.dominio.estadofichaperfil.error.no-encontrado";
        public static final String ERROR_ESTADO_TERMINAL = "fichas.dominio.estadofichaperfil.error.estado-terminal";

        public static final String LOG_CREADO = "fichas.aplicacion.estadofichaperfil.log.creado";
    }

    public static final class EvaluacionFichaPerfil {

        private EvaluacionFichaPerfil() {}

        public static final String ERROR_DUPLICADA = "fichas.dominio.evaluacionfichaperfil.error.duplicada";

        public static final String LOG_REGISTRADA = "fichas.aplicacion.evaluacionfichaperfil.log.registrada";
    }

    public static final class RepresentanteComite {

        private RepresentanteComite() {}

        public static final String ERROR_NO_ENCONTRADO = "fichas.dominio.representantecomite.error.no-encontrado";
    }

    public static final class EstadoEvaluacionFicha {

        private EstadoEvaluacionFicha() {}

        public static final String ERROR_EVALUACION_NO_ENCONTRADA = "fichas.dominio.estadoevaluacionficha.error.evaluacion-no-encontrada";
        public static final String ERROR_EVALUACION_NO_PROPIA = "fichas.dominio.estadoevaluacionficha.error.evaluacion-no-propia";
        public static final String ERROR_ESTADO_NO_ENCONTRADO = "fichas.dominio.estadoevaluacionficha.error.estado-no-encontrado";
        public static final String ERROR_ESTADO_DUPLICADO = "fichas.dominio.estadoevaluacionficha.error.estado-duplicado";
        public static final String ERROR_TRANSICION_DESDE_TERMINAL = "fichas.dominio.estadoevaluacionficha.error.transicion-desde-terminal";
        public static final String ERROR_EN_EVALUACION_NO_MANUAL = "fichas.dominio.estadoevaluacionficha.error.en-evaluacion-no-manual";

        public static final String LOG_AGREGADO = "fichas.aplicacion.estadoevaluacionficha.log.agregado";
        public static final String LOG_CREADO_AUTOMATICO = "fichas.aplicacion.estadoevaluacionficha.log.creado-automatico";
    }

    public static final class Usuario {

        private Usuario() {}

        public static final String LOG_USUARIO_CREADO_RECIBIDO = "fichas.infraestructura.usuario.log.usuario-creado-recibido";
        public static final String LOG_REGISTRADO_ESPEJO_SIMULADO = "fichas.infraestructura.usuario.log.registrado-espejo-simulado";
    }

    public static final class MinioGuia {

        private MinioGuia() {}

        public static final String LOG_UPLOAD_URL = "fichas.infraestructura.minioguia.log.upload-url";
        public static final String LOG_DOWNLOAD_URL = "fichas.infraestructura.minioguia.log.download-url";
        public static final String LOG_DELETE = "fichas.infraestructura.minioguia.log.delete";
    }
}
