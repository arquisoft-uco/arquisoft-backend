package com.arquisoft.shared.message.constant;

/**
 * Nombres de campo del contexto solicitudes.
 *
 * <p>Identifican el campo dentro de {@code fieldErrors[]} y dentro de los agregados al acumular
 * errores de validación (Notification Pattern). Son identificadores del contrato, así que se quedan
 * como constantes en lugar de salir al bundle.
 */
public final class SolicitudesFields {

    private SolicitudesFields() {}

    public static final class Solicitud {

        private Solicitud() {}

        public static final String ID = "solicitud";
        public static final String DESTINATARIO = "destinatario";
        public static final String REMITENTE = "remitente";
        public static final String MENSAJE = "mensajeSolicitud";
    }

    public static final class Remitente {

        private Remitente() {}

        public static final String REMITENTE = "remitente";
        public static final String USUARIO = "usuario";
    }

    public static final class Destinatario {

        private Destinatario() {}

        public static final String DESTINATARIO = "destinatario";
        public static final String USUARIO = "usuario";
    }

    public static final class Usuario {

        private Usuario() {}

        public static final String ID = "id";
        public static final String IDENTIFICADOR = "identificador";
        public static final String NOMBRE = "nombre";
        public static final String EMAIL = "email";
    }
}
