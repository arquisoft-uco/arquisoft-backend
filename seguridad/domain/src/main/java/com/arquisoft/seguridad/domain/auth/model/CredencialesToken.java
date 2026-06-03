package com.arquisoft.seguridad.domain.auth.model;

/**
 * Resultado de autenticacion o refresco de token contra el proveedor de identidad.
 * Tipo de dominio puro — sin Lombok, sin Spring.
 */
public record CredencialesToken(
        String accessToken,
        String refreshToken,
        long expiresIn,
        String tokenType,
        String scope
) {}
