package com.arquisoft.seguridad.application.auth.command.primaryport.mapper;

import com.arquisoft.seguridad.application.auth.command.primaryport.model.RefrescarTokenCommand;
import com.arquisoft.seguridad.domain.auth.TokenDomain;

public final class RefrescarTokenMapper {

    private RefrescarTokenMapper() {}

    public static TokenDomain toDomain(RefrescarTokenCommand command) {
        return TokenDomain.crear(command.refreshToken());
    }
}
