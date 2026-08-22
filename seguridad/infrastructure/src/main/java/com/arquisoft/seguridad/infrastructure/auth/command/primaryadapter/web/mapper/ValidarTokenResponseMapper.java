package com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.mapper;

import com.arquisoft.seguridad.application.auth.command.result.ValidacionTokenResult;
import com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.dto.ValidarTokenResponseDTO;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.seguridad.TokenKey;

public final class ValidarTokenResponseMapper {

    private ValidarTokenResponseMapper() {}

    public static ValidarTokenResponseDTO toResponse(ValidacionTokenResult result) {
        return switch (result) {
            case ValidacionTokenResult.Valida valida -> ValidarTokenResponseDTO.builder()
                    .valido(true)
                    .identidadId(valida.identidadId())
                    .correo(valida.correo())
                    .mensaje(Mensajes.obtener(TokenKey.LOG_VALIDO))
                    .build();
            case ValidacionTokenResult.Invalida invalida -> ValidarTokenResponseDTO.builder()
                    .valido(false)
                    .mensaje(Mensajes.obtener(TokenKey.LOG_INVALIDO))
                    .build();
        };
    }
}
