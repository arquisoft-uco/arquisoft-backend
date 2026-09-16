package com.arquisoft.shared.message.constant;

/**
 * Códigos de error del contexto solicitudes.
 *
 * <p>Contrato de la API, no texto: viajan en {@code ErrorResponseDTO.errorCode}. Ver la nota de
 * {@link AppCodes} sobre por qué no salen al bundle.
 */
public final class SolicitudesCodes {

    private SolicitudesCodes() {}

    public static final class Solicitud {

        private Solicitud() {}

        public static final String ID_REQUERIDO = "SOLICITUD_ID_REQUERIDO";
        public static final String MENSAJE_REQUERIDO = "SOLICITUD_MENSAJE_REQUERIDO";
        public static final String MENSAJE_DEMASIADO_LARGO = "SOLICITUD_MENSAJE_DEMASIADO_LARGO";
        public static final String DESTINATARIO_REQUERIDO = "SOLICITUD_DESTINATARIO_REQUERIDO";
        public static final String DESTINATARIO_NO_ENCONTRADO = "DESTINATARIO_NO_ENCONTRADO";
        public static final String DESTINATARIO_NO_ASIGNADO = "DESTINATARIO_NO_ASIGNADO";
        public static final String REMITENTE_REQUERIDO = "SOLICITUD_REMITENTE_REQUERIDO";
        public static final String REMITENTE_NO_ENCONTRADO = "REMITENTE_NO_ENCONTRADO";
        public static final String SOLICITUD_DUPLICADA = "SOLICITUD_DUPLICADA";
        public static final String SOLICITUD_NO_ENCONTRADA = "SOLICITUD_NO_ENCONTRADA";
        public static final String SOLICITUD_NO_PROPIA = "SOLICITUD_NO_PROPIA";
        public static final String SOLICITUD_TIPO_NO_COINCIDE = "SOLICITUD_TIPO_NO_COINCIDE";
        public static final String SOLICITUD_CON_RESPUESTAS = "SOLICITUD_CON_RESPUESTAS";
        public static final String SOLICITUD_NO_ES_DESTINATARIO = "SOLICITUD_NO_ES_DESTINATARIO";
    }

    public static final class Respuesta {

        private Respuesta() {}

        public static final String SOLICITUD_REQUERIDO = "RESPUESTA_SOLICITUD_REQUERIDO";
        public static final String CONTENIDO_REQUERIDO = "RESPUESTA_CONTENIDO_REQUERIDO";
        public static final String CONTENIDO_DEMASIADO_LARGO = "RESPUESTA_CONTENIDO_DEMASIADO_LARGO";
        public static final String SOLICITUD_YA_RESPONDIDA = "SOLICITUD_YA_RESPONDIDA";
        public static final String RESPUESTA_NO_ENCONTRADA = "RESPUESTA_NO_ENCONTRADA";
        public static final String RESPUESTA_NO_EN_REVISION = "RESPUESTA_NO_EN_REVISION";
        public static final String ESTADO_REQUERIDO = "RESPUESTA_ESTADO_REQUERIDO";
        public static final String ESTADO_NO_RESOLUTIVO = "RESPUESTA_ESTADO_NO_RESOLUTIVO";
    }

    public static final class EstadoRespuesta {

        private EstadoRespuesta() {}

        public static final String ESTADO_NO_ENCONTRADO = "ESTADO_RESPUESTA_NO_ENCONTRADO";
    }

    public static final class Remitente {

        private Remitente() {}

        public static final String REMITENTE_REQUERIDO = "REMITENTE_OBJETO_REQUERIDO";
        public static final String USUARIO_REQUERIDO = "REMITENTE_USUARIO_REQUERIDO";
    }

    public static final class Destinatario {

        private Destinatario() {}

        public static final String DESTINATARIO_REQUERIDO = "DESTINATARIO_OBJETO_REQUERIDO";
        public static final String USUARIO_REQUERIDO = "DESTINATARIO_USUARIO_REQUERIDO";
    }

    public static final class Usuario {

        private Usuario() {}

        public static final String ID_REQUERIDO = "USUARIO_REPLICA_ID_REQUERIDO";
        public static final String IDENTIFICADOR_REQUERIDO = "USUARIO_REPLICA_IDENTIFICADOR_REQUERIDO";
        public static final String NOMBRE_REQUERIDO = "USUARIO_REPLICA_NOMBRE_REQUERIDO";
        public static final String EMAIL_REQUERIDO = "USUARIO_REPLICA_EMAIL_REQUERIDO";
        public static final String OCURRIDO_EN_REQUERIDO = "USUARIO_REPLICA_OCURRIDO_EN_REQUERIDO";
    }

    public static final class TipoSolicitud {

        private TipoSolicitud() {}

        public static final String TIPO_NO_ENCONTRADO = "TIPO_SOLICITUD_NO_ENCONTRADO";
    }
}
