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

        public static final String ELIMINAR_NOVEDAD_COORDINADOR_SUMMARY =
                "Eliminar solicitud de novedad para el coordinador";
        public static final String ELIMINAR_NOVEDAD_COORDINADOR_DESCRIPTION =
                "Permite a un estudiante eliminar definitivamente una solicitud de novedad para el "
                        + "coordinador que envió por error.";
        public static final String ELIMINAR_NOVEDAD_COORDINADOR_RESP_204 = "Solicitud eliminada";
        public static final String ELIMINAR_NOVEDAD_COORDINADOR_RESP_400 =
                "Identificador de solicitud inválido";
        public static final String ELIMINAR_NOVEDAD_COORDINADOR_RESP_403 =
                "Sin permisos para eliminar solicitudes de novedad para el coordinador";
        public static final String ELIMINAR_NOVEDAD_COORDINADOR_RESP_422 =
                "Solicitud no encontrada, no propia, de otro tipo, o con respuestas";

        public static final String CONSULTAR_NOVEDAD_COORDINADOR_RECIBIDAS_SUMMARY =
                "Consultar solicitudes de novedad para el coordinador recibidas";
        public static final String CONSULTAR_NOVEDAD_COORDINADOR_RECIBIDAS_DESCRIPTION =
                "Permite a un coordinador consultar de forma paginada y filtrable las solicitudes de "
                        + "novedad para el coordinador que ha recibido. El alcance se limita a las "
                        + "solicitudes dirigidas al coordinador autenticado.";
        public static final String CONSULTAR_NOVEDAD_COORDINADOR_RECIBIDAS_RESP_200 =
                "Página de solicitudes de novedad para el coordinador recibidas";
        public static final String CONSULTAR_NOVEDAD_COORDINADOR_RECIBIDAS_RESP_400 =
                "Criterio de filtro, orden o paginación inválido";
        public static final String CONSULTAR_NOVEDAD_COORDINADOR_RECIBIDAS_RESP_403 =
                "Sin permisos para consultar solicitudes de novedad para el coordinador recibidas";

        public static final String CONSULTAR_NOVEDAD_COORDINADOR_ENVIADAS_SUMMARY =
                "Consultar solicitudes de novedad para el coordinador enviadas";
        public static final String CONSULTAR_NOVEDAD_COORDINADOR_ENVIADAS_DESCRIPTION =
                "Permite a un estudiante consultar de forma paginada y filtrable las solicitudes de "
                        + "novedad para el coordinador que ha enviado. El alcance se limita a las "
                        + "solicitudes cuyo remitente es el estudiante autenticado.";
        public static final String CONSULTAR_NOVEDAD_COORDINADOR_ENVIADAS_RESP_200 =
                "Página de solicitudes de novedad para el coordinador enviadas";
        public static final String CONSULTAR_NOVEDAD_COORDINADOR_ENVIADAS_RESP_400 =
                "Criterio de filtro, orden o paginación inválido";
        public static final String CONSULTAR_NOVEDAD_COORDINADOR_ENVIADAS_RESP_403 =
                "Sin permisos para consultar solicitudes de novedad para el coordinador enviadas";
    }

    public static final class Respuesta {

        private Respuesta() {}

        public static final String TAG_NAME = "Respuestas";
        public static final String TAG_DESCRIPTION = "Gestión de respuestas a las solicitudes";

        public static final String RESPONDER_NOVEDAD_COORDINADOR_SUMMARY =
                "Responder solicitud de novedad para el coordinador";
        public static final String RESPONDER_NOVEDAD_COORDINADOR_DESCRIPTION =
                "Permite a un coordinador responder una solicitud de novedad para el coordinador que "
                        + "le fue dirigida, registrando el contenido de la respuesta.";
        public static final String RESPONDER_NOVEDAD_COORDINADOR_RESP_201 =
                "Respuesta registrada — retorna el UUID asignado";
        public static final String RESPONDER_NOVEDAD_COORDINADOR_RESP_400 = "Datos inválidos";
        public static final String RESPONDER_NOVEDAD_COORDINADOR_RESP_403 =
                "Sin permisos para responder solicitudes de novedad para el coordinador";
        public static final String RESPONDER_NOVEDAD_COORDINADOR_RESP_422 =
                "Solicitud no encontrada, de otro tipo, dirigida a otro coordinador, o ya respondida";

        public static final String ELIMINAR_NOVEDAD_COORDINADOR_SUMMARY =
                "Eliminar respuesta de solicitud de novedad para el coordinador";
        public static final String ELIMINAR_NOVEDAD_COORDINADOR_DESCRIPTION =
                "Permite a un coordinador eliminar definitivamente la respuesta que registró para una "
                        + "solicitud de novedad para el coordinador, mientras siga en revisión.";
        public static final String ELIMINAR_NOVEDAD_COORDINADOR_RESP_204 = "Respuesta eliminada";
        public static final String ELIMINAR_NOVEDAD_COORDINADOR_RESP_400 =
                "Identificador de solicitud inválido";
        public static final String ELIMINAR_NOVEDAD_COORDINADOR_RESP_403 =
                "Sin permisos para eliminar respuestas de solicitudes de novedad para el coordinador";
        public static final String ELIMINAR_NOVEDAD_COORDINADOR_RESP_422 =
                "Solicitud no encontrada, de otro tipo, dirigida a otro coordinador; respuesta no "
                        + "encontrada o ya no está en revisión";

        public static final String MODIFICAR_ESTADO_NOVEDAD_COORDINADOR_SUMMARY =
                "Modificar estado de la respuesta de solicitud de novedad para el coordinador";
        public static final String MODIFICAR_ESTADO_NOVEDAD_COORDINADOR_DESCRIPTION =
                "Permite a un coordinador determinar el veredicto (aprobada o no aprobada) de la "
                        + "respuesta que registró para una solicitud de novedad para el coordinador que "
                        + "le fue dirigida, mientras siga en revisión.";
        public static final String MODIFICAR_ESTADO_NOVEDAD_COORDINADOR_RESP_204 = "Estado modificado";
        public static final String MODIFICAR_ESTADO_NOVEDAD_COORDINADOR_RESP_400 =
                "Identificador de solicitud inválido, o nuevo estado en blanco";
        public static final String MODIFICAR_ESTADO_NOVEDAD_COORDINADOR_RESP_403 =
                "Sin permisos para modificar el estado de respuestas de solicitudes de novedad para el "
                        + "coordinador";
        public static final String MODIFICAR_ESTADO_NOVEDAD_COORDINADOR_RESP_422 =
                "Solicitud no encontrada, de otro tipo, dirigida a otro coordinador; respuesta no "
                        + "encontrada, ya no está en revisión, o el nuevo estado no es resolutivo";

        public static final String CONSULTAR_NOVEDAD_COORDINADOR_RECIBIDAS_SUMMARY =
                "Consultar respuestas de solicitudes de novedad para el coordinador recibidas";
        public static final String CONSULTAR_NOVEDAD_COORDINADOR_RECIBIDAS_DESCRIPTION =
                "Permite a un estudiante consultar de forma paginada y filtrable las respuestas que ha "
                        + "recibido a sus solicitudes de novedad para el coordinador. El alcance se "
                        + "limita a las respuestas de solicitudes cuyo remitente es el estudiante "
                        + "autenticado.";
        public static final String CONSULTAR_NOVEDAD_COORDINADOR_RECIBIDAS_RESP_200 =
                "Página de respuestas de solicitudes de novedad para el coordinador recibidas";
        public static final String CONSULTAR_NOVEDAD_COORDINADOR_RECIBIDAS_RESP_400 =
                "Criterio de filtro, orden o paginación inválido";
        public static final String CONSULTAR_NOVEDAD_COORDINADOR_RECIBIDAS_RESP_403 =
                "Sin permisos para consultar respuestas de solicitudes de novedad para el coordinador "
                        + "recibidas";

        public static final String CONSULTAR_NOVEDAD_COORDINADOR_ENVIADAS_SUMMARY =
                "Consultar respuestas de solicitudes de novedad para el coordinador enviadas";
        public static final String CONSULTAR_NOVEDAD_COORDINADOR_ENVIADAS_DESCRIPTION =
                "Permite a un coordinador consultar de forma paginada y filtrable las respuestas que ha "
                        + "enviado a solicitudes de novedad para el coordinador. El alcance se limita a "
                        + "las respuestas de solicitudes cuyo destinatario es el coordinador autenticado.";
        public static final String CONSULTAR_NOVEDAD_COORDINADOR_ENVIADAS_RESP_200 =
                "Página de respuestas de solicitudes de novedad para el coordinador enviadas";
        public static final String CONSULTAR_NOVEDAD_COORDINADOR_ENVIADAS_RESP_400 =
                "Criterio de filtro, orden o paginación inválido";
        public static final String CONSULTAR_NOVEDAD_COORDINADOR_ENVIADAS_RESP_403 =
                "Sin permisos para consultar respuestas de solicitudes de novedad para el coordinador "
                        + "enviadas";
    }
}
