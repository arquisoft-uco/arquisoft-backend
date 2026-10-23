package com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web.mapper;

import com.arquisoft.seguridad.application.autorizacion.command.primaryport.model.AutorizarAccesoCommand;
import jakarta.servlet.http.HttpServletRequest;

public final class AutorizarAccesoRequestMapper {

    private AutorizarAccesoRequestMapper() {}

    public static AutorizarAccesoCommand toCommand(HttpServletRequest request, String recurso, String emisor,
                                                   String credencial, String correlacion) {
        return AutorizarAccesoCommand.crear(request.getMethod(), recurso, credencial, emisor, correlacion);
    }
}
