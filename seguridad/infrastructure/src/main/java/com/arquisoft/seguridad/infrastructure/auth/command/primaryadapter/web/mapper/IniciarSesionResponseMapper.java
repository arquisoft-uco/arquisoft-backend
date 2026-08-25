package com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.mapper;

import com.arquisoft.seguridad.application.auth.command.result.AutenticacionResult;
import com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.dto.IniciarSesionResponseDTO;

public final class IniciarSesionResponseMapper {

    private IniciarSesionResponseMapper() {}

    public static IniciarSesionResponseDTO toResponse(AutenticacionResult result) {
        return IniciarSesionResponseDTO.builder()
                .accessToken(result.accessToken())
                .refreshToken(result.refreshToken())
                .expiresIn(result.expiresIn())
                .tokenType(result.tokenType())
                .scope(result.scope())
                .build();
    }
}
