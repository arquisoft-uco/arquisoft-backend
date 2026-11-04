package com.arquisoft.seguridad.application.autenticacion.command.primaryport.mapper;

import com.arquisoft.seguridad.application.autenticacion.command.primaryport.model.RefrescarTokenCommand;
import com.arquisoft.seguridad.domain.autenticacion.TokenDomain;

public final class RefrescarTokenMapper {

    private RefrescarTokenMapper() {}

    public static TokenDomain toDomain(RefrescarTokenCommand command) {
        return TokenDomain.crear(command.refreshToken());
    }
}
