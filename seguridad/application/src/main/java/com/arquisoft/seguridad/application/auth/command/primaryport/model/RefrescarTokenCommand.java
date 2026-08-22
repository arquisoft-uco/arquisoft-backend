package com.arquisoft.seguridad.application.auth.command.primaryport.model;

import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.message.constant.SeguridadFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorTexto;

public record RefrescarTokenCommand(String refreshToken) {

    public RefrescarTokenCommand {
        refreshToken = UtilTexto.aplicarTrim(refreshToken);
    }

    public static RefrescarTokenCommand crear(String refreshToken) {
        var result = new ValidationResult();

        ValidatorTexto.noEnBlanco(refreshToken,
                SeguridadFields.Token.REFRESH_TOKEN,
                SeguridadCodes.Token.TOKEN_REFRESCO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RefrescarTokenCommand(refreshToken);
    }
}
