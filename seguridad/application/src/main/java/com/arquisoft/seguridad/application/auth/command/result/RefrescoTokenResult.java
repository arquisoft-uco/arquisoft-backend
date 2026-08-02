package com.arquisoft.seguridad.application.auth.command.result;

public record RefrescoTokenResult(
        String accessToken,
        String refreshToken,
        long expiresIn,
        String tokenType,
        String scope
) {}
