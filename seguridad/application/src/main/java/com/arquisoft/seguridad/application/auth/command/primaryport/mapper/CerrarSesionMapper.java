package com.arquisoft.seguridad.application.auth.command.primaryport.mapper;

import com.arquisoft.seguridad.application.auth.command.primaryport.model.TokenSesionCommand;
import com.arquisoft.seguridad.domain.auth.SesionDomain;

public final class CerrarSesionMapper {

    private CerrarSesionMapper() {}

    public static SesionDomain toDomain(TokenSesionCommand command) {
        return SesionDomain.crear(command.identificadorToken(), command.tiempoVidaRestante());
    }
}
