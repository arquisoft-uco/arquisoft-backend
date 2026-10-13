package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudCambioAsesorCommand;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaDomain;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaSolicitudDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

public final class ResponderSolicitudCambioAsesorMapper {

    private ResponderSolicitudCambioAsesorMapper() {}

    public static RespuestaSolicitudDomain toDomain(
            ResponderSolicitudCambioAsesorCommand command) {
        var respuesta = RespuestaDomain.crear(command.solicitud(), command.contenido());
        return RespuestaSolicitudDomain.crear(
                respuesta, command.coordinadorUsuario(), TipoSolicitud.CAMBIO_DE_ASESOR);
    }
}
