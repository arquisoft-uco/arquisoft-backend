package com.arquisoft.seguridad.domain.auth;

import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.message.constant.SeguridadFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorTexto;

public final class TokenDomain {

    private String valor;

    private TokenDomain() {}

    public static TokenDomain crear(String valor) {
        var token = new TokenDomain();
        var result = new ValidationResult();

        token.setValor(valor, result);

        result.lanzarSiTieneErrores();
        return token;
    }

    private void setValor(String valor, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(valor,
                SeguridadFields.Token.VALOR,
                SeguridadCodes.Token.TOKEN_VALOR_REQUERIDO, result)) {
            return;
        }
        this.valor = UtilTexto.aplicarTrim(valor);
    }

    public String getValor() {
        return valor;
    }
}
