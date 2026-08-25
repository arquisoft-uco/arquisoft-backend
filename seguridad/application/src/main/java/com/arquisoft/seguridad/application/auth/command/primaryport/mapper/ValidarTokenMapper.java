package com.arquisoft.seguridad.application.auth.command.primaryport.mapper;

import com.arquisoft.seguridad.application.auth.command.primaryport.model.ValidarTokenCommand;
import com.arquisoft.seguridad.domain.auth.TokenDomain;

public final class ValidarTokenMapper {

    private ValidarTokenMapper() {}

    public static TokenDomain toDomain(ValidarTokenCommand command) {
        return TokenDomain.crear(command.token());
    }
}
