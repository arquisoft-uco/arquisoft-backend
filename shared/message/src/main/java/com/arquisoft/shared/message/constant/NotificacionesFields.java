package com.arquisoft.shared.message.constant;

/**
 * Nombres de campo del contexto notificaciones. Ver la nota de {@link FichasFields}.
 */
public final class NotificacionesFields {

    private NotificacionesFields() {}

    public static final class Notificacion {

        private Notificacion() {}

        public static final String EVENT_ID = "eventId";
        public static final String TIPO = "tipo";
        public static final String DESTINATARIO = "destinatario";
        public static final String ASUNTO = "asunto";
        public static final String ESTADO = "estado";
    }
}
