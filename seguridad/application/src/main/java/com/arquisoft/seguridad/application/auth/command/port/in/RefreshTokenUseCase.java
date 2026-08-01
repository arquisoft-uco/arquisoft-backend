package com.arquisoft.seguridad.application.auth.command.port.in;

import com.arquisoft.shared.usecase.UseCase;

public interface RefreshTokenUseCase
        extends UseCase<String, RefreshTokenUseCase.RefreshResult> {

    record RefreshResult(
            String accessToken,
            String refreshToken,
            long expiresIn,
            String tokenType,
            String scope
    ) {}
}
