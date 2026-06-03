package com.arquisoft.seguridad.application.auth.command.port.in;

import com.arquisoft.shared.inputport.InputPort;

/**
 * Puerto de entrada — caso de uso para refrescar el token de acceso.
 */
public interface RefreshTokenInputPort
        extends InputPort<String, RefreshTokenInputPort.RefreshResult> {

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
}
