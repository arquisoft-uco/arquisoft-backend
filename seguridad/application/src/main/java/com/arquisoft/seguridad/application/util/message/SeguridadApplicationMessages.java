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
    // LogoutUseCase
    // =========================================================================

    public static final class LogoutUseCase {

        private LogoutUseCase() {}

        /**
         * log.info — Token invalidado exitosamente en la blacklist.
         * Parametros: identificador del token, TTL en segundos.
         */
        public static final String LOGOUT_EXITOSO =
                "Logout — token invalidado en blacklist: identificador='{}', TTL={}s";
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

    // =========================================================================
    // ValidateTokenUseCase
    // =========================================================================

    public static final class ValidateTokenUseCase {

        private ValidateTokenUseCase() {}

        /** log.debug — Entrada al caso de uso. El token no se loggea (dato sensible). */
        public static final String VALIDAR_DEBUG = "Intento de validacion de token";

        /** Mensaje de resultado cuando el token es valido. */
        public static final String TOKEN_VALIDO = "Token valido";

        /** Mensaje de resultado cuando el token es invalido o expirado. */
        public static final String TOKEN_INVALIDO = "Token invalido o expirado";

        /** log.debug — Token con error inesperado al validar. Parametro {}: detalle del error. */
        public static final String ERROR_VALIDAR = "Validacion de token fallida: {}";

        /** Prefijo del mensaje de resultado cuando ocurre un error inesperado. */
        public static final String ERROR_VALIDAR_PREFIJO = "Error al validar token: ";
    }
}
