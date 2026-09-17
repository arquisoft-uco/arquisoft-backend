package com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EliminarSolicitudNovedadAsesorCommand;
import com.arquisoft.solicitudes.domain.solicitud.EliminacionSolicitudNovedadAsesorDomain;

public final class EliminarSolicitudNovedadAsesorMapper {

    private EliminarSolicitudNovedadAsesorMapper() {}

    public static EliminacionSolicitudNovedadAsesorDomain toDomain(
            EliminarSolicitudNovedadAsesorCommand command) {
        return EliminacionSolicitudNovedadAsesorDomain.crear(
                command.solicitud(), command.remitenteUsuario());
    }
}
