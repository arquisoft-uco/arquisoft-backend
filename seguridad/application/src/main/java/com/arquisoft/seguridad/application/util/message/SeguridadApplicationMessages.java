package com.arquisoft.seguridad.application.util.message;

/**
 * Constantes de mensajes de log para la capa de aplicacion del contexto seguridad.
 *
 * <p>Organizado por clase consumidora. Ningun mensaje queda quemado en el codigo.</p>
 *
 * <p>Convencion de niveles de log:</p>
 * <ul>
 *   <li>{@code log.debug} — Entrada a metodos, parametros. Solo visible en perfil dev.</li>
 *   <li>{@code log.info}  — Evento de negocio completado exitosamente.</li>
 *   <li>{@code log.warn}  — Error de cliente: validacion fallida, datos invalidos.</li>
 *   <li>{@code log.error} — Error de servidor: servicio externo caido, error inesperado.</li>
 * </ul>
 */
public final class SeguridadApplicationMessages {

    private SeguridadApplicationMessages() {}

    // =========================================================================
    // LogoutRequestDTO
    // =========================================================================

    public static final class LogoutRequestDTO {

        private LogoutRequestDTO() {}

        // --- Logs (tecnicos, solo visibles en los logs del servidor) ---

        /**
         * log.warn — El campo 'jti' llego null al construir el DTO.
         * Indica un fallo en la guardia previa del controlador.
         */
        public static final String JTI_NULL_LOG =
                "LogoutRequestDTO: campo 'jti' es null — la guardia del controlador no funciono correctamente";

        /**
         * log.warn — El campo 'ttlSegundos' es <= 0.
         * Parametro {}: valor recibido para orientar el diagnostico.
         */
        public static final String TTL_INVALIDO_LOG =
                "LogoutRequestDTO: ttlSegundos debe ser > 0, recibido: {}";

        // --- ApplicationException (mensaje generico para el cliente, codigo para el desarrollador) ---

        /**
         * Mensaje generico expuesto al cliente en la respuesta HTTP (HTTP 400).
         * No revela nombres internos de campos ni detalles del JWT.
         */
        public static final String DATOS_SESION_INVALIDOS =
                "Datos de sesion invalidos. Intente autenticarse nuevamente.";

        /**
         * Codigo de error unico para identificar el fallo en la capa de aplicacion.
         * Mapeado por GlobalAppExceptionHandler a HTTP 400.
         */
        public static final String CODIGO_SESION_INVALIDA = "LOGOUT_SESION_INVALIDA";
    }

    // =========================================================================
    // AuthenticateUserUseCase
    // =========================================================================

    public static final class AuthenticateUserUseCase {

        private AuthenticateUserUseCase() {}

        /** log.debug — Entrada al caso de uso. Parametros no se loggean (datos sensibles). */
        public static final String AUTENTICAR_DEBUG = "Intento de autenticacion";

        /** log.info — Evento de negocio completado: credenciales validadas por Keycloak, tokens emitidos. */
        public static final String AUTENTICAR_EXITOSO = "Autenticacion exitosa";
    }

    // =========================================================================
    // RefreshTokenUseCase
    // =========================================================================

    public static final class RefreshTokenUseCase {

        private RefreshTokenUseCase() {}

        /** log.debug — Entrada al caso de uso. El refresh token no se loggea (dato sensible). */
        public static final String REFRESH_DEBUG = "Intento de refresco de token";

        /** log.info — Evento de negocio completado: nuevo access token emitido por Keycloak. */
        public static final String REFRESH_EXITOSO = "Token refrescado exitosamente";
    }
}
