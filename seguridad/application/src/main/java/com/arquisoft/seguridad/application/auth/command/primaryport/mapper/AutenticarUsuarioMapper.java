package com.arquisoft.seguridad.application.auth.command.primaryport.mapper;

import com.arquisoft.seguridad.application.auth.command.primaryport.model.AutenticarUsuarioCommand;
import com.arquisoft.seguridad.domain.auth.AutenticacionDomain;

public final class AutenticarUsuarioMapper {

    private AutenticarUsuarioMapper() {}

    public static AutenticacionDomain toDomain(AutenticarUsuarioCommand command) {
        return AutenticacionDomain.crear(command.email(), command.contrasena());
    }
}
