package com.arquisoft.usuarios.application.usuario.command.primaryport.mapper;

import com.arquisoft.usuarios.application.usuario.command.primaryport.model.CambiarEstadoUsuarioCommand;
import com.arquisoft.usuarios.domain.usuario.CambioEstadoUsuarioDomain;

public final class CambiarEstadoUsuarioMapper {

    private CambiarEstadoUsuarioMapper() {}

    public static CambioEstadoUsuarioDomain toDomain(CambiarEstadoUsuarioCommand command) {
        return CambioEstadoUsuarioDomain.crear(command.usuario(), command.estado());
    }
}
