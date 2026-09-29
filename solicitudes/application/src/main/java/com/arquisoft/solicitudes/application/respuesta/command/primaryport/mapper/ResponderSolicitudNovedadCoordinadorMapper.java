package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaDomain;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaNovedadCoordinadorDomain;

public final class ResponderSolicitudNovedadCoordinadorMapper {

    private ResponderSolicitudNovedadCoordinadorMapper() {}

    public static RespuestaNovedadCoordinadorDomain toDomain(
            ResponderSolicitudNovedadCoordinadorCommand command) {
        var respuesta = RespuestaDomain.crear(command.solicitud(), command.contenido());
        return RespuestaNovedadCoordinadorDomain.crear(respuesta, command.coordinadorUsuario());
    }
}
