package com.arquisoft.seguridad.application.auth.command.port.in;

import com.arquisoft.shared.inputport.InputPort;

public interface RefreshTokenInputPort
        extends InputPort<String, RefreshTokenInputPort.RefreshResult> {

    record RefreshResult(
            String accessToken,
            String refreshToken,
            long expiresIn,
            String tokenType,
            String scope
    ) {}
}
