package com.arquisoft.seguridad.infrastructure.autenticacion.command.primaryadapter.web.mapper;

import com.arquisoft.seguridad.application.autenticacion.command.primaryport.model.ValidarTokenCommand;

public final class ValidarTokenRequestMapper {

    private ValidarTokenRequestMapper() {}

    public static ValidarTokenCommand toCommand(String token) {
        return ValidarTokenCommand.crear(token);
    }
}
