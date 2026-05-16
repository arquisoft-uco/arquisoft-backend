package com.arquisoft.seguridad.application.auth.command;

/**
 * Caso de uso para refrescar el token de acceso.
 */
public interface RefreshTokenUseCase {

    /**
     * Resultado del refresco de token.
     */
    record RefreshResult(
            String accessToken,
            String refreshToken,
            long expiresIn,
            String tokenType,
            String scope
    ) {}

    /**
     * Refresca el access token usando un refresh token valido.
     *
     * @param refreshToken el refresh token
     * @return resultado con los nuevos tokens
     * @throws com.arquisoft.seguridad.domain.exception.InvalidTokenException
     *         si el refresh token es invalido o expirado
     */
    RefreshResult refresh(String refreshToken);
}
