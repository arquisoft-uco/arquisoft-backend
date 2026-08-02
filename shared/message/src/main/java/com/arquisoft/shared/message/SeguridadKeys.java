package com.arquisoft.shared.message;

/**
 * Claves del catálogo para el contexto seguridad — bundle {@code messages/seguridad.properties}.
 */
public final class SeguridadKeys {

    private SeguridadKeys() {}

    public static final class Login {

        private Login() {}

        public static final String ERROR_AUTENTICAR_KEYCLOAK = "seguridad.dominio.login.error.autenticar-keycloak";
        public static final String ERROR_CREDENCIALES_INVALIDAS = "seguridad.dominio.login.error.credenciales-invalidas";
        public static final String ERROR_COMUNICACION_KEYCLOAK = "seguridad.dominio.login.error.comunicacion-keycloak";
        public static final String ERROR_SERVICIO_NO_DISPONIBLE = "seguridad.dominio.login.error.servicio-no-disponible";
        public static final String ERROR_INESPERADO_AUTENTICACION = "seguridad.dominio.login.error.inesperado-autenticacion";
        public static final String ERROR_HTTP_401 = "seguridad.infraestructura.login.error.http-401";

        public static final String LOG_CREDENCIALES_INVALIDAS = "seguridad.infraestructura.login.log.credenciales-invalidas";
        public static final String LOG_ERROR_AUTENTICACION_KEYCLOAK = "seguridad.infraestructura.login.log.error-autenticacion-keycloak";
        public static final String LOG_KEYCLOAK_NO_DISPONIBLE = "seguridad.infraestructura.login.log.keycloak-no-disponible";
        public static final String LOG_ERROR_INESPERADO = "seguridad.infraestructura.login.log.error-inesperado";
        public static final String LOG_CREDENCIALES_INVALIDAS_HANDLER = "seguridad.infraestructura.login.log.credenciales-invalidas-handler";
        public static final String LOG_EXCEPCION_AUTENTICACION = "seguridad.infraestructura.login.log.excepcion-autenticacion";
        public static final String LOG_ACCESS_DENIED = "seguridad.infraestructura.login.log.access-denied";
        public static final String LOG_UNAUTHORIZED = "seguridad.infraestructura.login.log.unauthorized";
        public static final String LOG_JWT_DECODER_CONFIG = "seguridad.infraestructura.login.log.jwt-decoder-config";
    }

    public static final class Token {

        private Token() {}

        public static final String ERROR_VALOR_REQUERIDO = "seguridad.dominio.token.error.valor-requerido";
        public static final String ERROR_REFRESCAR = "seguridad.dominio.token.error.refrescar";
        public static final String ERROR_REFRESH_INVALIDO_EXPIRADO = "seguridad.dominio.token.error.refresh-invalido-expirado";
        public static final String ERROR_REFRESCAR_DETALLE = "seguridad.dominio.token.error.refrescar-detalle";
        public static final String ERROR_INESPERADO_REFRESCO = "seguridad.dominio.token.error.inesperado-refresco";
        public static final String ERROR_INVALIDO_DETALLE = "seguridad.dominio.token.error.invalido-detalle";
        public static final String ERROR_VALIDAR_DETALLE = "seguridad.dominio.token.error.validar-detalle";

        public static final String LOG_VALIDAR_DEBUG = "seguridad.aplicacion.token.log.validar-debug";
        public static final String LOG_VALIDO = "seguridad.aplicacion.token.log.valido";
        public static final String LOG_INVALIDO = "seguridad.aplicacion.token.log.invalido";
        public static final String LOG_VALIDACION_FALLIDA = "seguridad.aplicacion.token.log.validacion-fallida";
        public static final String LOG_REFRESH_DEBUG = "seguridad.aplicacion.token.log.refresh-debug";
        public static final String LOG_REFRESH_EXITOSO = "seguridad.aplicacion.token.log.refresh-exitoso";
        public static final String LOG_REFRESH_INVALIDO = "seguridad.infraestructura.token.log.refresh-invalido";
        public static final String LOG_ERROR_REFRESCO_KEYCLOAK = "seguridad.infraestructura.token.log.error-refresco-keycloak";
        public static final String LOG_KEYCLOAK_NO_DISPONIBLE_REFRESCO = "seguridad.infraestructura.token.log.keycloak-no-disponible-refresco";
        public static final String LOG_ERROR_INESPERADO_REFRESCO = "seguridad.infraestructura.token.log.error-inesperado-refresco";
        public static final String LOG_VALIDACION_REFRESH_FALLIDA = "seguridad.infraestructura.token.log.validacion-refresh-fallida";
        public static final String LOG_ERROR_EXTRAER_INFO = "seguridad.infraestructura.token.log.error-extraer-info";
        public static final String LOG_INVALIDO_HANDLER = "seguridad.infraestructura.token.log.invalido-handler";
    }

    public static final class Sesion {

        private Sesion() {}

        public static final String ERROR_IDENTIFICADOR_REQUERIDO = "seguridad.dominio.sesion.error.identificador-requerido";
        public static final String ERROR_TTL_INVALIDO = "seguridad.dominio.sesion.error.ttl-invalido";

        public static final String LOG_LOGOUT_EXITOSO = "seguridad.aplicacion.sesion.log.logout-exitoso";
    }

    public static final class Identidad {

        private Identidad() {}

        public static final String ERROR_ID_REQUERIDO = "seguridad.dominio.identidad.error.id-requerido";
        public static final String ERROR_CORREO_REQUERIDO = "seguridad.dominio.identidad.error.correo-requerido";
    }

    public static final class Credenciales {

        private Credenciales() {}

        public static final String ERROR_TOKEN_ACCESO_REQUERIDO = "seguridad.dominio.credenciales.error.token-acceso-requerido";
        public static final String ERROR_EXPIRACION_INVALIDA = "seguridad.dominio.credenciales.error.expiracion-invalida";
        public static final String ERROR_TIPO_TOKEN_REQUERIDO = "seguridad.dominio.credenciales.error.tipo-token-requerido";
    }

    public static final class Autenticacion {

        private Autenticacion() {}

        public static final String LOG_AUTENTICAR_DEBUG = "seguridad.aplicacion.autenticacion.log.autenticar-debug";
        public static final String LOG_AUTENTICAR_EXITOSO = "seguridad.aplicacion.autenticacion.log.autenticar-exitoso";
        public static final String LOG_REFRESH_EXITOSO = "seguridad.aplicacion.autenticacion.log.refresh-exitoso";
        public static final String LOG_VALIDATE_DEBUG = "seguridad.aplicacion.autenticacion.log.validate-debug";
    }

    public static final class RateLimit {

        private RateLimit() {}

        public static final String ERROR_LIMITE_EXCEDIDO = "seguridad.infraestructura.ratelimit.error.limite-excedido";
        public static final String ERROR_HTTP_TOO_MANY_REQUESTS = "seguridad.infraestructura.ratelimit.error.http-too-many-requests";
        public static final String ERROR_CLIENTE_STANDALONE = "seguridad.infraestructura.ratelimit.error.cliente-standalone";

        public static final String LOG_LIMITE_EXCEDIDO = "seguridad.infraestructura.ratelimit.log.limite-excedido";
        public static final String LOG_INIT_OK = "seguridad.infraestructura.ratelimit.log.init-ok";
        public static final String LOG_CLIENTE_STANDALONE_ERROR = "seguridad.infraestructura.ratelimit.log.cliente-standalone-error";
        public static final String LOG_BUCKET_REDIS_ERROR = "seguridad.infraestructura.ratelimit.log.bucket-redis-error";
        public static final String LOG_BUCKET_LOGIN_REDIS_ERROR = "seguridad.infraestructura.ratelimit.log.bucket-login-redis-error";
    }

    public static final class JwtBlacklist {

        private JwtBlacklist() {}

        public static final String ERROR_HTTP_401 = "seguridad.infraestructura.jwtblacklist.error.http-401";
        public static final String ERROR_HTTP_401_DETALLE = "seguridad.infraestructura.jwtblacklist.error.http-401-detalle";
        public static final String ERROR_HTTP_503 = "seguridad.infraestructura.jwtblacklist.error.http-503";
        public static final String ERROR_HTTP_503_DETALLE = "seguridad.infraestructura.jwtblacklist.error.http-503-detalle";

        public static final String LOG_TOKEN_REVOCADO = "seguridad.infraestructura.jwtblacklist.log.token-revocado";
        public static final String LOG_REDIS_NO_DISPONIBLE = "seguridad.infraestructura.jwtblacklist.log.redis-no-disponible";
    }

    public static final class Rol {

        private Rol() {}

        public static final String LOG_RESOURCE_ROLES = "seguridad.infraestructura.rol.log.resource-roles";
    }
}
