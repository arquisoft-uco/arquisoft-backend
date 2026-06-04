package com.arquisoft.seguridad.domain.auth.model;

import com.arquisoft.shared.exception.DomainException;

public record CredencialesSesion(
        String tokenAcceso,
        String tokenRefresco,
        long expiraEn,
        String tipoToken,
        String alcance) {

    public CredencialesSesion {
        if (tokenAcceso == null || tokenAcceso.isBlank()) {
            throw new DomainException("El token de acceso no puede ser nulo ni vacio",
                    "CREDENCIALES_TOKEN_ACCESO_REQUERIDO");
        }
        if (expiraEn <= 0) {
            throw new DomainException("El tiempo de expiracion debe ser mayor a cero",
                    "CREDENCIALES_EXPIRACION_INVALIDA");
        }
        if (tipoToken == null || tipoToken.isBlank()) {
            throw new DomainException("El tipo de token no puede ser nulo ni vacio",
                    "CREDENCIALES_TIPO_TOKEN_REQUERIDO");
        }
    }

    public static CredencialesSesion de(
            String tokenAcceso,
            String tokenRefresco,
            long expiraEn,
            String tipoToken,
            String alcance) {
        return new CredencialesSesion(tokenAcceso, tokenRefresco, expiraEn, tipoToken, alcance);
    }
}
