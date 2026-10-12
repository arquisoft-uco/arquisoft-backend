package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaDomain;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaSolicitudDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

public final class ResponderSolicitudNovedadCoordinadorMapper {

    private ResponderSolicitudNovedadCoordinadorMapper() {}

    public static RespuestaSolicitudDomain toDomain(
            ResponderSolicitudNovedadCoordinadorCommand command) {
        var respuesta = RespuestaDomain.crear(command.solicitud(), command.contenido());
        return RespuestaSolicitudDomain.crear(
                respuesta, command.coordinadorUsuario(), TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);
    }
}
