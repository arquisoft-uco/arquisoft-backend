package com.arquisoft.shared.message.annotation;

/**
 * Textos de documentación OpenAPI del contexto {@code seguridad}.
 *
 * <p>Mismo criterio que {@link FichasApiMessages}: el texto va incrustado y no al catálogo de Redis,
 * porque la especificación OpenAPI se construye una vez al arrancar y no vuelve a consultarse. Ver
 * allí el razonamiento completo.
 *
 * <p>Estos textos ya estaban incrustados —literales dentro de cada anotación—, así que lo que aporta
 * esta clase no es sacarlos del código sino sacarlos del adaptador: un controlador no declara
 * literales, y tener los de los tres contextos bajo el mismo paquete es lo que permite revisarlos
 * juntos.
 */
public final class SeguridadApiMessages {

    private SeguridadApiMessages() {}

    public static final class Autenticacion {

        private Autenticacion() {}

        public static final String TAG_NAME = "Seguridad - Autenticacion";
        public static final String TAG_DESCRIPTION = "Comandos de autenticacion: login, refresh y logout";

        public static final String INICIAR_SESION_DEPRECADO_DESDE =
                "OAuth 2.1 / RFC 9700 — usar Authorization Code + PKCE en la SPA";
        public static final String INICIAR_SESION_SUMMARY = "Iniciar sesion (ROPC — desaconsejado para navegadores)";
        public static final String INICIAR_SESION_DESCRIPTION =
                "Autentica al usuario contra Keycloak usando email y contrasena "
                        + "(grant_type=password / ROPC). DESACONSEJADO por OAuth 2.1 y RFC 9700: "
                        + "el flujo recomendado para la SPA es Authorization Code + PKCE contra "
                        + "Keycloak. Este endpoint se mantiene solo para clientes internos de confianza. "
                        + "Retorna access token, refresh token y metadatos de la sesion.";
        public static final String INICIAR_SESION_RESP_200 = "Autenticacion exitosa — tokens retornados";
        public static final String INICIAR_SESION_RESP_400 = "Datos de entrada invalidos";
        public static final String INICIAR_SESION_RESP_401 = "Credenciales incorrectas";

        public static final String REFRESCAR_SUMMARY = "Refrescar token";
        public static final String REFRESCAR_DESCRIPTION = "Obtiene un nuevo access token usando un refresh token valido.";
        public static final String REFRESCAR_RESP_200 = "Token refrescado exitosamente";
        public static final String REFRESCAR_RESP_400 = "Refresh token ausente o invalido";
        public static final String REFRESCAR_RESP_401 = "Refresh token expirado o revocado";

        public static final String CERRAR_SESION_SUMMARY = "Cerrar sesion";
        public static final String CERRAR_SESION_DESCRIPTION = "Invalida el token JWT actual en la blacklist de Redis.";
        public static final String CERRAR_SESION_RESP_200 = "Sesion cerrada";
        public static final String CERRAR_SESION_RESP_400 = "Token de sesion invalido o ya expirado";
        public static final String CERRAR_SESION_RESP_401 = "No autenticado";

        public static final String VALIDAR_SUMMARY = "Validar token JWT";
        public static final String VALIDAR_DESCRIPTION =
                "Valida un token JWT sin requerirlo en el header Authorization. "
                        + "Util para validaciones internas entre servicios.";
        public static final String VALIDAR_RESP_200 = "Resultado de la validacion del token";
        public static final String VALIDAR_RESP_400 = "Parametro token ausente";
    }
}
