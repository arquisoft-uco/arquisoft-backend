package com.arquisoft.shared.message;

public final class SeguridadMessages {

    private SeguridadMessages() {}

    // ─────────────────────────────────────────────────────────────────────────
    // Login
    // ─────────────────────────────────────────────────────────────────────────

    public static final class Login {

        private Login() {}

        // Códigos de error
        public static final String AUTENTICACION_ERROR             = "AUTENTICACION_ERROR";
        public static final String CREDENCIALES_INVALIDAS           = "CREDENCIALES_INVALIDAS";
        public static final String PROVEEDOR_IDENTIDAD_NO_DISPONIBLE = "PROVEEDOR_IDENTIDAD_NO_DISPONIBLE";

        // Mensajes de error
        public static final String ERROR_AUTENTICAR_KEYCLOAK_MSG   = "Error al autenticar con Keycloak";
        public static final String CREDENCIALES_INVALIDAS_MSG      = "Credenciales invalidas";
        public static final String ERROR_COMUNICACION_KEYCLOAK     = "Error al comunicarse con Keycloak: ";
        public static final String SERVICIO_NO_DISPONIBLE_MSG      = "Servicio de autenticacion no disponible temporalmente";
        public static final String ERROR_INESPERADO_AUTENTICACION  = "Error inesperado durante la autenticacion: ";
        public static final String HTTP_401_ERROR                  = "Unauthorized";

        // Logs
        public static final String LOG_CREDENCIALES_INVALIDAS      = "Credenciales invalidas";
        public static final String LOG_ERROR_AUTENTICACION_KEYCLOAK = "Error de autenticacion en Keycloak: {} - {}";
        public static final String LOG_KEYCLOAK_NO_DISPONIBLE      = "Keycloak no disponible (timeout/red) al autenticar: {}";
        public static final String LOG_ERROR_INESPERADO            = "Error inesperado durante la autenticacion: {}";
        public static final String LOG_CREDENCIALES_INVALIDAS_HANDLER = "Credenciales invalidas en {}: [{}] {}";
        public static final String LOG_EXCEPCION_AUTENTICACION     = "Excepcion de autenticacion en {}: [{}] {}";
        public static final String LOG_ACCESS_DENIED               = "Access denied (filter-level) in {}: {}";
        public static final String LOG_UNAUTHORIZED                = "Unauthorized (filter-level) in {}: {}";
        public static final String LOG_JWT_DECODER_CONFIG          = "Configuring JWT decoder with issuer: {} (expected audience: {})";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Token
    // ─────────────────────────────────────────────────────────────────────────

    public static final class Token {

        private Token() {}

        // Códigos de error
        public static final String TOKEN_VALOR_REQUERIDO = "TOKEN_VALOR_REQUERIDO";
        public static final String TOKEN_INVALIDO_CODIGO = "TOKEN_INVALIDO";

        // Mensajes de error
        public static final String VALOR_REQUERIDO_MSG   = "El token no puede ser nulo ni vacio";
        public static final String ERROR_REFRESCAR_MSG    = "Error al refrescar el token";
        public static final String REFRESH_INVALIDO_EXPIRADO_MSG = "Refresh token invalido o expirado";
        public static final String ERROR_REFRESCAR_PREFIJO = "Error al refrescar el token: ";
        public static final String ERROR_INESPERADO_REFRESCO_PREFIJO = "Error inesperado al refrescar el token: ";
        public static final String TOKEN_INVALIDO_PREFIJO  = "Token invalido: ";

        // Logs
        public static final String VALIDAR_DEBUG          = "Intento de validacion de token";
        public static final String TOKEN_VALIDO           = "Token valido";
        public static final String TOKEN_INVALIDO         = "Token invalido o expirado";
        public static final String ERROR_VALIDAR          = "Validacion de token fallida: {}";
        public static final String ERROR_VALIDAR_PREFIJO  = "Error al validar token: ";
        public static final String REFRESH_DEBUG          = "Intento de refresco de token";
        public static final String REFRESH_EXITOSO        = "Token refrescado exitosamente";
        public static final String LOG_REFRESH_INVALIDO   = "Refresh token invalido";
        public static final String LOG_ERROR_REFRESCO_KEYCLOAK = "Error de refresco de token en Keycloak: {} - {}";
        public static final String LOG_KEYCLOAK_NO_DISPONIBLE_REFRESCO = "Keycloak no disponible (timeout/red) al refrescar token: {}";
        public static final String LOG_ERROR_INESPERADO_REFRESCO = "Error inesperado al refrescar el token: {}";
        public static final String LOG_VALIDACION_REFRESH_FALLIDA = "Validacion de refresh token fallida: {}";
        public static final String LOG_ERROR_EXTRAER_INFO  = "Error al extraer informacion del token: {}";
        public static final String LOG_VALIDACION_FALLIDA  = "Validacion de token fallida: {}";
        public static final String LOG_TOKEN_INVALIDO_HANDLER = "Token invalido en {}: [{}] {}";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Sesion
    // ─────────────────────────────────────────────────────────────────────────

    public static final class Sesion {

        private Sesion() {}

        // Códigos de error
        public static final String SESION_IDENTIFICADOR_REQUERIDO = "SESION_IDENTIFICADOR_REQUERIDO";
        public static final String SESION_TTL_INVALIDO            = "SESION_TTL_INVALIDO";

        // Mensajes de error
        public static final String IDENTIFICADOR_REQUERIDO_MSG = "El identificador del token no puede ser nulo ni vacio";
        public static final String TTL_INVALIDO_MSG         = "El tiempo de vida restante debe ser mayor a cero";

        // Logs
        public static final String LOGOUT_EXITOSO = "Logout — token invalidado en blacklist: identificador='{}', TTL={}s";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Identidad
    // ─────────────────────────────────────────────────────────────────────────

    public static final class Identidad {

        private Identidad() {}

        // Códigos de error
        public static final String IDENTIDAD_ID_REQUERIDO     = "IDENTIDAD_ID_REQUERIDO";
        public static final String IDENTIDAD_CORREO_REQUERIDO = "IDENTIDAD_CORREO_REQUERIDO";

        // Mensajes de error
        public static final String ID_REQUERIDO_MSG     = "El identificador de identidad no puede ser nulo ni vacio";
        public static final String CORREO_REQUERIDO_MSG = "El correo no puede ser nulo ni vacio";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Credenciales
    // ─────────────────────────────────────────────────────────────────────────

    public static final class Credenciales {

        private Credenciales() {}

        // Códigos de error
        public static final String CREDENCIALES_TOKEN_ACCESO_REQUERIDO = "CREDENCIALES_TOKEN_ACCESO_REQUERIDO";
        public static final String CREDENCIALES_EXPIRACION_INVALIDA    = "CREDENCIALES_EXPIRACION_INVALIDA";
        public static final String CREDENCIALES_TIPO_TOKEN_REQUERIDO   = "CREDENCIALES_TIPO_TOKEN_REQUERIDO";

        // Mensajes de error
        public static final String TOKEN_ACCESO_REQUERIDO_MSG = "El token de acceso no puede ser nulo ni vacio";
        public static final String EXPIRACION_INVALIDA_MSG    = "El tiempo de expiracion debe ser mayor a cero";
        public static final String TIPO_TOKEN_REQUERIDO_MSG   = "El tipo de token no puede ser nulo ni vacio";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Autenticacion (AuthenticateUserUseCase / AuthCommandInputAdapter)
    // ─────────────────────────────────────────────────────────────────────────

    public static final class Autenticacion {

        private Autenticacion() {}

        // Logs
        public static final String AUTENTICAR_DEBUG      = "Intento de autenticacion";
        public static final String AUTENTICAR_EXITOSO    = "Autenticacion exitosa";
        public static final String REFRESH_EXITOSO_DEBUG = "Token refrescado exitosamente";
        public static final String VALIDATE_DEBUG        = "Intento de validacion de token";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RateLimit (RedisBucketResolver / RateLimitingFilter)
    // ─────────────────────────────────────────────────────────────────────────

    public static final class RateLimit {

        private RateLimit() {}

        // Códigos de error
        public static final String RATE_LIMIT_EXCEDIDO = "RATE_LIMIT_EXCEEDED";
        public static final String REDIS_CLIENTE_STANDALONE_REQUERIDO = "REDIS_CLIENTE_STANDALONE_REQUERIDO";

        // Mensajes de error
        public static final String LIMITE_EXCEDIDO_PREFIJO = "Has excedido el límite de solicitudes. Intenta de nuevo en ";
        public static final String LIMITE_EXCEDIDO_SUFIJO  = " segundos.";
        public static final String HTTP_TOO_MANY_REQUESTS  = "Too Many Requests";
        public static final String CLIENTE_STANDALONE_EXCEPCION_MSG =
                "Error al inicializar el servicio de limitacion de peticiones. Contacte al administrador del sistema.";

        // Logs
        public static final String LOG_RATE_LIMIT_EXCEDIDO = "Rate limit exceeded for IP: {} on endpoint: {}";
        public static final String INIT_OK = "RedisBucketResolver inicializado (rate limiting distribuido via Bucket4j + Lettuce/Redis)";
        public static final String CLIENTE_STANDALONE_ERROR_LOG =
                "Fallo al inicializar RedisBucketResolver: se requiere RedisClient standalone "
                + "pero se obtuvo [{}]. Verifique la configuracion de spring.data.redis.*";
        public static final String BUCKET_REDIS_ERROR       = "Error al obtener bucket Redis para IP={}, aplicando fail-closed: {}";
        public static final String BUCKET_LOGIN_REDIS_ERROR = "Error al obtener login bucket Redis para IP={}, aplicando fail-closed: {}";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // JwtBlacklist (JwtBlacklistFilter)
    // ─────────────────────────────────────────────────────────────────────────

    public static final class JwtBlacklist {

        private JwtBlacklist() {}

        // Mensajes de error
        public static final String HTTP_401_ERROR   = "Unauthorized";
        public static final String HTTP_401_MESSAGE = "No autenticado o token invalido";
        public static final String HTTP_503_ERROR   = "Service Unavailable";
        public static final String HTTP_503_MESSAGE = "Servicio temporalmente no disponible, intente mas tarde";

        // Logs
        public static final String TOKEN_REVOCADO_LOG      = "Token revocado detectado: JTI='{}' path='{}'";
        public static final String REDIS_NO_DISPONIBLE_LOG = "Servicio de blacklist Redis no disponible, denegando request (fail-closed): {}";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Rol (KeycloakJwtConverterConfig)
    // ─────────────────────────────────────────────────────────────────────────

    public static final class Rol {

        private Rol() {}

        // Logs
        public static final String LOG_RESOURCE_ROLES = "Resource roles: {}";
    }
}
