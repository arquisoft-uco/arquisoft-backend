package com.arquisoft.seguridad.domain.auth.aggregate;

import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.message.SeguridadCodes;
import com.arquisoft.shared.message.SeguridadKeys;
import com.arquisoft.shared.exception.DomainException;

public final class TokenDomain {

    private final String valor;

    private TokenDomain(String valor) {
        this.valor = valor;
    }

    public static TokenDomain de(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new DomainException(Messages.obtener(SeguridadKeys.Token.ERROR_VALOR_REQUERIDO), SeguridadCodes.Token.TOKEN_VALOR_REQUERIDO);
        }
        return new TokenDomain(valor);
    }

    public String valor() {
        return valor;
    }
}
