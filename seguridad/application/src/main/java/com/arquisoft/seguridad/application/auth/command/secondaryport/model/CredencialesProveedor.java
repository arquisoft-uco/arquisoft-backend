package com.arquisoft.seguridad.application.auth.command.secondaryport.model;

public record CredencialesProveedor(
        String tokenAcceso,
        String tokenRefresco,
        long expiraEn,
        String tipoToken,
        String alcance) {
}
