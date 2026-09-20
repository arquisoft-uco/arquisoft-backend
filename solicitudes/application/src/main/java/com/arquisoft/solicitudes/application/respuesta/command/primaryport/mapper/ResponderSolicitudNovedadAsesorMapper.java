package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudNovedadAsesorCommand;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaNovedadAsesorDomain;

public final class ResponderSolicitudNovedadAsesorMapper {

    private ResponderSolicitudNovedadAsesorMapper() {}

    public static RespuestaNovedadAsesorDomain toDomain(
            ResponderSolicitudNovedadAsesorCommand command) {
        return RespuestaNovedadAsesorDomain.crear(
                command.solicitud(), command.contenido(), command.asesorUsuario());
    }
}
