package com.arquisoft.usuarios.infrastructure.usuario.command.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.usuario.command.primaryport.model.CambiarEstadoUsuarioCommand;
import com.arquisoft.usuarios.infrastructure.usuario.command.primaryadapter.web.dto.CambiarEstadoUsuarioRequestDTO;

import java.util.UUID;

public final class CambiarEstadoUsuarioRequestMapper {

    private CambiarEstadoUsuarioRequestMapper() {}

    public static CambiarEstadoUsuarioCommand toCommand(UUID usuarioId, CambiarEstadoUsuarioRequestDTO request) {
        return CambiarEstadoUsuarioCommand.crear(usuarioId, request.estado());
    }
}
