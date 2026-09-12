package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaNovedadCoordinadorDomain;

public final class ResponderSolicitudNovedadCoordinadorMapper {

    private ResponderSolicitudNovedadCoordinadorMapper() {}

    public static RespuestaNovedadCoordinadorDomain toDomain(
            ResponderSolicitudNovedadCoordinadorCommand command) {
        return RespuestaNovedadCoordinadorDomain.crear(
                command.solicitud(), command.contenido(), command.coordinadorUsuario());
    }
}
