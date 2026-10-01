package com.arquisoft.seguridad.application.auth.command.primaryport.model;

import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.message.constant.SeguridadFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorTexto;

public record ValidarTokenCommand(String token) {

    public ValidarTokenCommand {
        token = UtilTexto.aplicarTrim(token);
    }

    public static ValidarTokenCommand crear(String token) {
        var result = new ValidationResult();

        ValidatorTexto.noEnBlanco(token,
                SeguridadFields.Token.VALOR,
                SeguridadCodes.Token.TOKEN_VALOR_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ValidarTokenCommand(token);
    }
}
