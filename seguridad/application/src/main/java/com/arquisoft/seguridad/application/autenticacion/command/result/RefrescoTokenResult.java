package com.arquisoft.seguridad.application.autenticacion.command.result;

public record RefrescoTokenResult(
        String accessToken,
        String refreshToken,
        long expiresIn,
        String tokenType,
        String scope
) {}
