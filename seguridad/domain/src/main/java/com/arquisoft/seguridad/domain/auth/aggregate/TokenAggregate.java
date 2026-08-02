package com.arquisoft.seguridad.domain.auth.aggregate;

import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.message.SeguridadCodes;
import com.arquisoft.shared.message.SeguridadKeys;
import com.arquisoft.shared.exception.DomainException;

public final class TokenAggregate {

    private final String valor;

    private TokenAggregate(String valor) {
        this.valor = valor;
    }

    public static TokenAggregate de(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new DomainException(Messages.obtener(SeguridadKeys.Token.ERROR_VALOR_REQUERIDO), SeguridadCodes.Token.TOKEN_VALOR_REQUERIDO);
        }
        return new TokenAggregate(valor);
    }

    public String valor() {
        return valor;
    }
}
