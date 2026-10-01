package com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.mapper;

import com.arquisoft.seguridad.application.auth.command.primaryport.model.AutenticarUsuarioCommand;
import com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.dto.IniciarSesionRequestDTO;

public final class IniciarSesionRequestMapper {

    private IniciarSesionRequestMapper() {}

    public static AutenticarUsuarioCommand toCommand(IniciarSesionRequestDTO dto) {
        return AutenticarUsuarioCommand.crear(dto.email(), dto.contrasena());
    }
}
