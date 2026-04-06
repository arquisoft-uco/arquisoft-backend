package com.arquisoft.seguridad.domain.port.in;

/**
 * Caso de uso para autenticar un usuario.
 * Recibe credenciales primitivas; la traduccion desde/hacia DTOs
 * ocurre en la implementacion de la capa de aplicacion.
 */
public interface AuthenticateUserUseCase {

    /**
     * Resultado de la autenticacion representado como record (Java 21).
     * Tipo de dominio puro, sin dependencias de framework.
     */
    record AuthResult(
            String accessToken,
            String refreshToken,
            long expiresIn,
            String tokenType,
            String scope
    ) {}

    /**
     * Autentica al usuario con email y contrasena.
     *
     * @param email    email del usuario
     * @param password contrasena del usuario
     * @return resultado con los tokens de autenticacion
     * @throws com.arquisoft.seguridad.domain.exception.InvalidCredentialsException
     *         si las credenciales son invalidas
     */
    AuthResult authenticate(String email, String password);
}
