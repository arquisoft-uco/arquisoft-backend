package com.arquisoft.seguridad.domain.auth.aggregate;

import com.arquisoft.shared.message.key.seguridad.TokenKey;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.exception.DomainException;

public final class TokenDomain {

    private final String valor;

    private TokenDomain(String valor) {
        this.valor = valor;
    }

    public static TokenDomain de(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new DomainException(Messages.obtener(TokenKey.ERROR_VALOR_REQUERIDO), SeguridadCodes.Token.TOKEN_VALOR_REQUERIDO);
        }
        return new TokenDomain(valor);
    }

    public String valor() {
        return valor;
    }
}
