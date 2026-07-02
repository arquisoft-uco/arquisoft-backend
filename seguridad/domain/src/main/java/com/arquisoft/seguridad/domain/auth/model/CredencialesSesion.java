package com.arquisoft.seguridad.domain.auth.model;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.SeguridadMessages;

public record CredencialesSesion(
        String tokenAcceso,
        String tokenRefresco,
        long expiraEn,
        String tipoToken,
        String alcance) {

    public CredencialesSesion {
        if (tokenAcceso == null || tokenAcceso.isBlank()) {
            throw new DomainException(SeguridadMessages.Credenciales.TOKEN_ACCESO_REQUERIDO,
                    SeguridadMessages.Credenciales.CREDENCIALES_TOKEN_ACCESO_REQUERIDO);
        }
        if (expiraEn <= 0) {
            throw new DomainException(SeguridadMessages.Credenciales.EXPIRACION_INVALIDA,
                    SeguridadMessages.Credenciales.CREDENCIALES_EXPIRACION_INVALIDA);
        }
        if (tipoToken == null || tipoToken.isBlank()) {
            throw new DomainException(SeguridadMessages.Credenciales.TIPO_TOKEN_REQUERIDO,
                    SeguridadMessages.Credenciales.CREDENCIALES_TIPO_TOKEN_REQUERIDO);
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
