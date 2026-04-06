package com.arquisoft.seguridad.domain.port.out;

/**
 * Puerto de salida para autenticacion contra el servidor de identidad (Keycloak).
 * El dominio define el contrato; la infraestructura implementa la comunicacion
 * con el proveedor de identidad externo.
 *
 * Nota: Los tipos de parametros y retorno son Map<String, Object> para mantener
 * el dominio libre de DTOs de aplicacion. La traduccion a DTOs ocurre en la
 * capa de aplicacion (casos de uso).
 */
public interface AuthenticationPort {

    /**
     * Autentica al usuario contra el proveedor de identidad usando email y contrasena.
     *
     * @param email    email del usuario
     * @param password contrasena del usuario
     * @return mapa con los campos del token (access_token, refresh_token, expires_in, etc.)
     * @throws com.arquisoft.seguridad.domain.exception.InvalidCredentialsException
     *         si las credenciales son invalidas
     * @throws com.arquisoft.seguridad.domain.exception.AuthenticationException
     *         si hay error en la comunicacion con el proveedor
     */
    java.util.Map<String, Object> authenticate(String email, String password);

    /**
     * Refresca el token de acceso usando el refresh token.
     *
     * @param refreshToken el refresh token
     * @return mapa con los campos del nuevo token
     * @throws com.arquisoft.seguridad.domain.exception.InvalidTokenException
     *         si el refresh token es invalido o expirado
     */
    java.util.Map<String, Object> refreshToken(String refreshToken);

    /**
     * Valida que el refresh token sea valido.
     *
     * @param refreshToken el refresh token
     * @return true si es valido
     */
    boolean validateRefreshToken(String refreshToken);
}
