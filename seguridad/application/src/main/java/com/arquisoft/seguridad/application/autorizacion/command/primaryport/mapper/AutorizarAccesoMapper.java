package com.arquisoft.seguridad.application.autorizacion.command.primaryport.mapper;

import com.arquisoft.seguridad.application.autorizacion.command.primaryport.model.AutorizarAccesoCommand;
import com.arquisoft.seguridad.domain.autorizacion.AccesoDomain;

public final class AutorizarAccesoMapper {

    private AutorizarAccesoMapper() {}

    public static AccesoDomain toDomain(AutorizarAccesoCommand command) {
        return AccesoDomain.crear(command.metodo(), command.recurso(), command.credencial(),
                command.emisor(), command.correlacion());
    }
}
