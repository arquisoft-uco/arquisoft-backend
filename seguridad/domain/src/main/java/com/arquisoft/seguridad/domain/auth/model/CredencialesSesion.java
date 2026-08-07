package com.arquisoft.seguridad.domain.auth.model;

import com.arquisoft.shared.message.key.seguridad.CredencialesKey;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.exception.DomainException;

public record CredencialesSesion(
        String tokenAcceso,
        String tokenRefresco,
        long expiraEn,
        String tipoToken,
        String alcance) {

    public CredencialesSesion {
        if (tokenAcceso == null || tokenAcceso.isBlank()) {
            throw new DomainException(Mensajes.obtener(CredencialesKey.ERROR_TOKEN_ACCESO_REQUERIDO),
                    SeguridadCodes.Credenciales.CREDENCIALES_TOKEN_ACCESO_REQUERIDO);
        }
        if (expiraEn <= 0) {
            throw new DomainException(Mensajes.obtener(CredencialesKey.ERROR_EXPIRACION_INVALIDA),
                    SeguridadCodes.Credenciales.CREDENCIALES_EXPIRACION_INVALIDA);
        }
        if (tipoToken == null || tipoToken.isBlank()) {
            throw new DomainException(Mensajes.obtener(CredencialesKey.ERROR_TIPO_TOKEN_REQUERIDO),
                    SeguridadCodes.Credenciales.CREDENCIALES_TIPO_TOKEN_REQUERIDO);
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
