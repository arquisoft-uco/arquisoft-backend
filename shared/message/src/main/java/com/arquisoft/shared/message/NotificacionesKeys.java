package com.arquisoft.shared.message;

/**
 * Claves del catálogo para el contexto notificaciones — bundle
 * {@code messages/notificaciones.properties}.
 */
public final class NotificacionesKeys {

    private NotificacionesKeys() {}

    /** Reglas del agregado {@code NotificacionDomain}. */
    public static final class Notificacion {

        private Notificacion() {}

        public static final String ERROR_TRANSICION_INVALIDA =
                "notificaciones.dominio.notificacion.error.transicion-invalida";

        public static final String LOG_EVENTO_DUPLICADO =
                "notificaciones.aplicacion.notificacion.log.evento-duplicado";
        public static final String LOG_ENVIADA =
                "notificaciones.aplicacion.notificacion.log.enviada";
        public static final String LOG_FALLIDA =
                "notificaciones.aplicacion.notificacion.log.fallida";
    }

    /** Textos de los correos que produce el contexto. */
    public static final class Plantilla {

        private Plantilla() {}

        public static final String ASUNTO_ASESOR_CAMBIADO =
                "notificaciones.aplicacion.plantilla.asunto.asesor-cambiado";
        public static final String CUERPO_ASESOR_CAMBIADO =
                "notificaciones.aplicacion.plantilla.cuerpo.asesor-cambiado";
    }

    /** Consumidores AMQP. */
    public static final class Consumidor {

        private Consumidor() {}

        public static final String LOG_ASESOR_CAMBIADO_RECIBIDO =
                "notificaciones.infraestructura.consumidor.log.asesor-cambiado-recibido";
    }
}
