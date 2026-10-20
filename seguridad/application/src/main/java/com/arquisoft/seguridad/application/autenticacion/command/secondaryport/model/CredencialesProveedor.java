package com.arquisoft.seguridad.application.autenticacion.command.secondaryport.model;

public record CredencialesProveedor(
        String tokenAcceso,
        String tokenRefresco,
        long expiraEn,
        String tipoToken,
        String alcance) {
}
