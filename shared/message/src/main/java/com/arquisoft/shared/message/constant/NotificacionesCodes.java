package com.arquisoft.shared.message.constant;

/**
 * Códigos de error del contexto notificaciones. Ver la nota de {@link AppCodes}.
 */
public final class NotificacionesCodes {

    private NotificacionesCodes() {}

    public static final class Notificacion {

        private Notificacion() {}

        public static final String ID_EVENTO_REQUERIDO = "NOTIFICACION_ID_EVENTO_REQUERIDO";
        public static final String TIPO_REQUERIDO = "NOTIFICACION_TIPO_REQUERIDO";
        public static final String DESTINATARIO_REQUERIDO = "NOTIFICACION_DESTINATARIO_REQUERIDO";
        public static final String DESTINATARIO_INVALIDO = "NOTIFICACION_DESTINATARIO_INVALIDO";
        public static final String ASUNTO_REQUERIDO = "NOTIFICACION_ASUNTO_REQUERIDO";
        public static final String TRANSICION_INVALIDA = "NOTIFICACION_TRANSICION_INVALIDA";
        public static final String REINTENTO_NO_PERMITIDO = "NOTIFICACION_REINTENTO_NO_PERMITIDO";
        public static final String TIPO_NO_ENCONTRADO = "NOTIFICACION_TIPO_NO_ENCONTRADO";
        public static final String ESTADO_NO_ENCONTRADO = "NOTIFICACION_ESTADO_NO_ENCONTRADO";
        public static final String DESTINATARIO_NOMBRE_REQUERIDO = "NOTIFICACION_DESTINATARIO_NOMBRE_REQUERIDO";
        public static final String CUERPO_REQUERIDO = "NOTIFICACION_CUERPO_REQUERIDO";
        public static final String MAX_INTENTOS_INVALIDO = "NOTIFICACION_MAX_INTENTOS_INVALIDO";
        public static final String LIMITE_INVALIDO = "NOTIFICACION_LIMITE_INVALIDO";
        public static final String PLANTILLA_NO_DISPONIBLE = "NOTIFICACION_PLANTILLA_NO_DISPONIBLE";
        public static final String PLANTILLA_CORREO_NO_DISPONIBLE = "NOTIFICACION_PLANTILLA_CORREO_NO_DISPONIBLE";
    }
}
