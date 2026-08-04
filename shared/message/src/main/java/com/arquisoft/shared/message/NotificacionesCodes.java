package com.arquisoft.shared.message;

/**
 * Códigos de error del contexto notificaciones. Ver la nota de {@link AppCodes}.
 */
public final class NotificacionesCodes {

    private NotificacionesCodes() {}

    public static final class Notificacion {

        private Notificacion() {}

        public static final String EVENT_ID_REQUERIDO = "NOTIFICACION_EVENT_ID_REQUERIDO";
        public static final String TIPO_REQUERIDO = "NOTIFICACION_TIPO_REQUERIDO";
        public static final String DESTINATARIO_REQUERIDO = "NOTIFICACION_DESTINATARIO_REQUERIDO";
        public static final String DESTINATARIO_INVALIDO = "NOTIFICACION_DESTINATARIO_INVALIDO";
        public static final String ASUNTO_REQUERIDO = "NOTIFICACION_ASUNTO_REQUERIDO";
        public static final String TRANSICION_INVALIDA = "NOTIFICACION_TRANSICION_INVALIDA";
    }
}
