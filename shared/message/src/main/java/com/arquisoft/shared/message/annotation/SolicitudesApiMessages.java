package com.arquisoft.shared.message.annotation;

/**
 * Textos de documentación OpenAPI del contexto solicitudes, listos para {@code @Tag},
 * {@code @Operation} y {@code @ApiResponse}.
 *
 * <p>Van incrustados aquí y no en el catálogo de Redis: un valor de anotación tiene que ser una
 * expresión constante (JLS §9.7.1) y la especificación OpenAPI se congela al arrancar, así que no
 * gana nada de ADR-013 y sí pagaría una clave más en el fail-fast de arranque.
 */
public final class SolicitudesApiMessages {

    private SolicitudesApiMessages() {}

    public static final class Comun {

        private Comun() {}

        public static final String RESP_401 = "No autenticado";
        public static final String RESP_403 = "Sin permisos para realizar esta acción";
    }

    public static final class Solicitud {

        private Solicitud() {}

        public static final String TAG_NAME = "Solicitudes";
        public static final String TAG_DESCRIPTION = "Gestión de solicitudes de los estudiantes";

        public static final String ENVIAR_NOVEDAD_COORDINADOR_SUMMARY =
                "Enviar solicitud de novedad para el coordinador";
        public static final String ENVIAR_NOVEDAD_COORDINADOR_DESCRIPTION =
                "Permite a un estudiante enviar una solicitud de novedad dirigida a un coordinador.";
        public static final String ENVIAR_NOVEDAD_COORDINADOR_RESP_201 =
                "Solicitud enviada — retorna el UUID asignado";
        public static final String ENVIAR_NOVEDAD_COORDINADOR_RESP_400 = "Datos inválidos";
        public static final String ENVIAR_NOVEDAD_COORDINADOR_RESP_422 =
                "Remitente o destinatario no encontrado, o solicitud duplicada";

        public static final String ENVIAR_NOVEDAD_ASESOR_SUMMARY =
                "Enviar solicitud de novedad para el asesor";
        public static final String ENVIAR_NOVEDAD_ASESOR_DESCRIPTION =
                "Permite a un estudiante enviar una solicitud de novedad dirigida a un asesor.";
        public static final String ENVIAR_NOVEDAD_ASESOR_RESP_201 =
                "Solicitud enviada — retorna el UUID asignado";
        public static final String ENVIAR_NOVEDAD_ASESOR_RESP_400 = "Datos inválidos";
        public static final String ENVIAR_NOVEDAD_ASESOR_RESP_422 =
                "Remitente o destinatario no encontrado, o solicitud duplicada";

        public static final String ENVIAR_CAMBIO_ASESOR_SUMMARY =
                "Enviar solicitud para cambio de asesor";
        public static final String ENVIAR_CAMBIO_ASESOR_DESCRIPTION =
                "Permite a un estudiante enviar una solicitud para justificar un cambio de asesor, "
                        + "dirigida a un coordinador.";
        public static final String ENVIAR_CAMBIO_ASESOR_RESP_201 =
                "Solicitud enviada — retorna el UUID asignado";
        public static final String ENVIAR_CAMBIO_ASESOR_RESP_400 = "Datos inválidos";
        public static final String ENVIAR_CAMBIO_ASESOR_RESP_422 =
                "Remitente o destinatario no encontrado, o solicitud duplicada";

        public static final String ENVIAR_AMPLIACION_PLAZO_SUMMARY =
                "Enviar solicitud para ampliación de plazo";
        public static final String ENVIAR_AMPLIACION_PLAZO_DESCRIPTION =
                "Permite a un estudiante enviar una solicitud para justificar la ampliación del plazo "
                        + "de entrega de su proyecto de grado, dirigida a un coordinador.";
        public static final String ENVIAR_AMPLIACION_PLAZO_RESP_201 =
                "Solicitud enviada — retorna el UUID asignado";
        public static final String ENVIAR_AMPLIACION_PLAZO_RESP_400 = "Datos inválidos";
        public static final String ENVIAR_AMPLIACION_PLAZO_RESP_422 =
                "Remitente o destinatario no encontrado, o solicitud duplicada";
    }
}
