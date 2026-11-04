package com.arquisoft.seguridad.application.autenticacion.command.result;

public record AutenticacionResult(
        String accessToken,
        String refreshToken,
        long expiresIn,
        String tokenType,
        String scope
) {}
