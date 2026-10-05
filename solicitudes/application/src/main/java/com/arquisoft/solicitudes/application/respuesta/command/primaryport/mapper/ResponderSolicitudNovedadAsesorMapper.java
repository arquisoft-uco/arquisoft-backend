package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudNovedadAsesorCommand;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaDomain;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaSolicitudDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

public final class ResponderSolicitudNovedadAsesorMapper {

    private ResponderSolicitudNovedadAsesorMapper() {}

    public static RespuestaSolicitudDomain toDomain(
            ResponderSolicitudNovedadAsesorCommand command) {
        var respuesta = RespuestaDomain.crear(command.solicitud(), command.contenido());
        return RespuestaSolicitudDomain.crear(
                respuesta, command.asesorUsuario(), TipoSolicitud.NOVEDAD_PARA_EL_ASESOR);
    }
}
