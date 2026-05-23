package com.arquisoft.seguridad.infrastructure.util.message;

/**
 * Constantes de mensajes de log y respuesta HTTP para la capa de infraestructura
 * del contexto seguridad.
 *
 * <p>Organizado por clase consumidora para facilitar la navegacion y el mantenimiento.
 * Ningun mensaje queda quemado directamente en el codigo de produccion.</p>
 *
 * <p>Convencion de niveles de log:</p>
 * <ul>
 *   <li>{@code log.debug} — Entrada a metodos, parametros. Solo visible en perfil dev.</li>
 *   <li>{@code log.info}  — Evento de negocio completado exitosamente.</li>
 *   <li>{@code log.warn}  — Error de cliente: credenciales invalidas, token expirado,
 *                           rate limit excedido, validacion fallida.</li>
 *   <li>{@code log.error} — Error de servidor: servicio externo caido, error inesperado.</li>
 * </ul>
 */
public final class SeguridadInfraestructureMessages {

    private SeguridadInfraestructureMessages() {}

    // =========================================================================
    // RedisBucketResolver
    // =========================================================================

    public static final class RedisBucketResolver {

        private RedisBucketResolver() {}

        // --- Logs ---

        /** log.debug — Inicializacion correcta del ProxyManager distribuido. */
        public static final String INIT_OK =
                "RedisBucketResolver inicializado (rate limiting distribuido via Bucket4j + Lettuce/Redis)";

        /**
         * log.error — Fallo de configuracion al inicializar: el cliente Lettuce no es standalone.
         * Parametro {}: nombre de la clase real del cliente obtenido (ej. RedisClusterClient).
         * Mensaje tecnico — solo visible en los logs del servidor, nunca en la respuesta HTTP.
         */
        public static final String CLIENTE_STANDALONE_ERROR_LOG =
                "Fallo al inicializar RedisBucketResolver: se requiere RedisClient standalone "
                + "pero se obtuvo [{}]. Verifique la configuracion de spring.data.redis.*";

        /** log.error — Redis no disponible al resolver bucket general; se aplica fail-closed. */
        public static final String BUCKET_REDIS_ERROR =
                "Error al obtener bucket Redis para IP={}, aplicando fail-closed: {}";

        /** log.error — Redis no disponible al resolver bucket de login; se aplica fail-closed. */
        public static final String BUCKET_LOGIN_REDIS_ERROR =
                "Error al obtener login bucket Redis para IP={}, aplicando fail-closed: {}";

        // --- Excepcion de infraestructura ---

        /**
         * Codigo de error para InfrastructureException cuando Lettuce no es standalone.
         * Mapeado por GlobalAppExceptionHandler a HTTP 503.
         */
        public static final String CLIENTE_STANDALONE_CODIGO =
                "REDIS_CLIENTE_STANDALONE_REQUERIDO";

        /**
         * Mensaje generico de InfrastructureException para el cliente.
         * No expone detalles tecnicos internos (tipo de cliente, configuracion, etc.).
         * Si la excepcion llegara a la capa web, el cliente solo ve este texto.
         */
        public static final String CLIENTE_STANDALONE_EXCEPCION =
                "Error al inicializar el servicio de limitacion de peticiones. "
                + "Contacte al administrador del sistema.";
    }

    // =========================================================================
    // JwtBlacklistFilter
    // =========================================================================

    public static final class JwtBlacklistFilter {

        private JwtBlacklistFilter() {}

        // --- Logs ---

        /** log.warn — Token revocado detectado; request bloqueado. No se expone JTI en la respuesta al cliente. */
        public static final String TOKEN_REVOCADO_LOG =
                "Token revocado detectado: JTI='{}' path='{}'";

        /** log.error — Redis no disponible al verificar blacklist; se aplica fail-closed (request denegado). */
        public static final String REDIS_NO_DISPONIBLE_LOG =
                "Servicio de blacklist Redis no disponible, denegando request (fail-closed): {}";

        // --- Respuestas HTTP (genericas: no revelan la razon especifica de la denegacion) ---

        /** Campo 'error' para respuestas 401. Coincide con GlobalAppExceptionHandler.handleAuthenticationException. */
        public static final String HTTP_401_ERROR = "Unauthorized";

        /**
         * Mensaje 401 generico.
         * No se indica que el token fue revocado por logout para evitar
         * filtrar informacion sobre el estado de sesion del usuario (OWASP A01).
         */
        public static final String HTTP_401_MESSAGE = "No autenticado o token invalido";

        /** Campo 'error' para respuestas 503 de Redis caido. */
        public static final String HTTP_503_ERROR = "Service Unavailable";

        /** Mensaje 503 generico para el cliente. No expone detalles internos de infraestructura. */
        public static final String HTTP_503_MESSAGE =
                "Servicio temporalmente no disponible, intente mas tarde";
    }

    // =========================================================================
    // AuthInputAdapter
    // =========================================================================

    public static final class AuthInputAdapter {

        private AuthInputAdapter() {}

        // --- Logs de refresh token ---

        /** log.debug — Entrada al endpoint de refresco. El refresh token no se loggea (dato sensible). */
        public static final String REFRESH_DEBUG = "Intento de refresco de token";

        /**
         * log.debug — Refresco completado en el adaptador de entrada.
         * El caso de uso RefreshTokenUseCaseImpl ya emite log.info del exito del negocio;
         * este debug es a nivel de capa web para trazar el flujo del request.
         */
        public static final String REFRESH_EXITOSO_DEBUG = "Token refrescado exitosamente";

        // --- Logs de validacion de token ---

        /** log.debug — Entrada al endpoint de validacion. El token no se loggea (dato sensible). */
        public static final String VALIDATE_DEBUG = "Intento de validacion de token";

        // --- Logs de logout ---

        /**
         * log.warn — Token presentado en logout no contiene claim 'jti'.
         * No deberia ocurrir con tokens Keycloak validos; indica un token no estandar.
         */
        public static final String LOGOUT_SIN_JTI =
                "Logout con token sin claim 'jti' — invalidacion omitida";

        /**
         * log.warn — Token ya expirado al momento del logout.
         * No es necesario agregar a blacklist; expiro de forma natural.
         */
        public static final String LOGOUT_TOKEN_EXPIRADO =
                "Logout con token ya expirado o sin expiracion: JTI='{}'";

        /**
         * log.info — Evento de negocio completado: token invalidado exitosamente en Redis.
         * Parametros: JTI del token, TTL en segundos hasta expiracion natural.
         */
        public static final String LOGOUT_EXITOSO =
                "Logout — token invalidado en blacklist: JTI='{}', TTL={}s";
    }
}
