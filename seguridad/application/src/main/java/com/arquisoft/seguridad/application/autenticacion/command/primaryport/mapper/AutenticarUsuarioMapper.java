package com.arquisoft.seguridad.application.autenticacion.command.primaryport.mapper;

import com.arquisoft.seguridad.application.autenticacion.command.primaryport.model.AutenticarUsuarioCommand;
import com.arquisoft.seguridad.domain.autenticacion.AutenticacionDomain;

public final class AutenticarUsuarioMapper {

    private AutenticarUsuarioMapper() {}

    public static AutenticacionDomain toDomain(AutenticarUsuarioCommand command) {
        return AutenticacionDomain.crear(command.email(), command.contrasena());
    }
}
