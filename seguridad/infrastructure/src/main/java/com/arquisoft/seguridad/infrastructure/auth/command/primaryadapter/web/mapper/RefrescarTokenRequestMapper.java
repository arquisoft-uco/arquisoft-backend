package com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.mapper;

import com.arquisoft.seguridad.application.auth.command.primaryport.model.RefrescarTokenCommand;
import com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.dto.RefrescarTokenRequestDTO;

public final class RefrescarTokenRequestMapper {

    private RefrescarTokenRequestMapper() {}

    public static RefrescarTokenCommand toCommand(RefrescarTokenRequestDTO dto) {
        return RefrescarTokenCommand.crear(dto.refreshToken());
    }
}
