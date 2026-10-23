package com.arquisoft.seguridad.application.autenticacion.command.primaryport.mapper;

import com.arquisoft.seguridad.application.autenticacion.command.primaryport.model.TokenSesionCommand;
import com.arquisoft.seguridad.domain.autenticacion.SesionDomain;

public final class CerrarSesionMapper {

    private CerrarSesionMapper() {}

    public static SesionDomain toDomain(TokenSesionCommand command) {
        return SesionDomain.crear(command.identificadorToken(), command.tiempoVidaRestante());
    }
}
