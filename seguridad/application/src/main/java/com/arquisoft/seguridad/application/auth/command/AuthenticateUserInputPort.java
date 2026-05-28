package com.arquisoft.seguridad.application.auth.command;

import com.arquisoft.shared.inputport.InputPort;

/**
 * Puerto de entrada — caso de uso para autenticar un usuario.
 * Recibe un {@link AuthenticateUserCommand} con las credenciales; la traduccion
 * desde/hacia DTOs ocurre en la capa de infraestructura.
 */
public interface AuthenticateUserInputPort
        extends InputPort<AuthenticateUserCommand, AuthenticateUserInputPort.AuthResult> {

    /**
     * Resultado de la autenticacion representado como record (Java 21).
     * Tipo de aplicacion puro, sin dependencias de framework.
     */
    record AuthResult(
            String accessToken,
            String refreshToken,
            long expiresIn,
            String tokenType,
            String scope
    ) {}
}
