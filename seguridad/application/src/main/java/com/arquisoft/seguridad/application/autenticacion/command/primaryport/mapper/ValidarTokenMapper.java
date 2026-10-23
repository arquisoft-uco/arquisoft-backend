package com.arquisoft.seguridad.application.autenticacion.command.primaryport.mapper;

import com.arquisoft.seguridad.application.autenticacion.command.primaryport.model.ValidarTokenCommand;
import com.arquisoft.seguridad.domain.autenticacion.TokenDomain;

public final class ValidarTokenMapper {

    private ValidarTokenMapper() {}

    public static TokenDomain toDomain(ValidarTokenCommand command) {
        return TokenDomain.crear(command.token());
    }
}
