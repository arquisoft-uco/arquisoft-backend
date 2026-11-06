package com.arquisoft.seguridad.application.autenticacion.command.primaryport.mapper;

import com.arquisoft.seguridad.application.autenticacion.command.primaryport.model.ResolverIdentidadSesionCommand;
import com.arquisoft.seguridad.domain.autenticacion.CredencialSesionDomain;

public final class ResolverIdentidadSesionMapper {

    private ResolverIdentidadSesionMapper() {}

    public static CredencialSesionDomain toDomain(ResolverIdentidadSesionCommand command) {
        return CredencialSesionDomain.crear(command.credencial());
    }
}
