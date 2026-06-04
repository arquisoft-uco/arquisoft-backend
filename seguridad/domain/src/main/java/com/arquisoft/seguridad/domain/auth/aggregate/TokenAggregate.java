package com.arquisoft.seguridad.domain.auth.aggregate;

import com.arquisoft.shared.exception.DomainException;

public final class TokenAggregate {

    private final String valor;

    private TokenAggregate(String valor) {
        this.valor = valor;
    }

    public static TokenAggregate de(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new DomainException("El token no puede ser nulo ni vacio", "TOKEN_VALOR_REQUERIDO");
        }
        return new TokenAggregate(valor);
    }

    public String valor() {
        return valor;
    }
}
