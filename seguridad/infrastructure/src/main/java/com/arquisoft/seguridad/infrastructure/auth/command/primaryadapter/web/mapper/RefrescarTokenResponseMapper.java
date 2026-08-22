package com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.mapper;

import com.arquisoft.seguridad.application.auth.command.result.RefrescoTokenResult;
import com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.dto.RefrescarTokenResponseDTO;

public final class RefrescarTokenResponseMapper {

    private RefrescarTokenResponseMapper() {}

    public static RefrescarTokenResponseDTO toResponse(RefrescoTokenResult result) {
        return RefrescarTokenResponseDTO.builder()
                .accessToken(result.accessToken())
                .refreshToken(result.refreshToken())
                .expiresIn(result.expiresIn())
                .tokenType(result.tokenType())
                .scope(result.scope())
                .build();
    }
}
