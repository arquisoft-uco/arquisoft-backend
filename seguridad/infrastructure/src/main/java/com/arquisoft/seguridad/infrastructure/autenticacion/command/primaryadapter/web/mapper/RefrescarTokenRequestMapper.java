package com.arquisoft.seguridad.infrastructure.autenticacion.command.primaryadapter.web.mapper;

import com.arquisoft.seguridad.application.autenticacion.command.primaryport.model.RefrescarTokenCommand;
import com.arquisoft.seguridad.infrastructure.autenticacion.command.primaryadapter.web.dto.RefrescarTokenRequestDTO;

public final class RefrescarTokenRequestMapper {

    private RefrescarTokenRequestMapper() {}

    public static RefrescarTokenCommand toCommand(RefrescarTokenRequestDTO dto) {
        return RefrescarTokenCommand.crear(dto.refreshToken());
    }
}
