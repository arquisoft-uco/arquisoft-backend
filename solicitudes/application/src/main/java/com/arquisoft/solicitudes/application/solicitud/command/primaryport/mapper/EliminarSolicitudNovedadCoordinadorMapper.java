package com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EliminarSolicitudNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.domain.solicitud.EliminacionSolicitudNovedadCoordinadorDomain;

public final class EliminarSolicitudNovedadCoordinadorMapper {

    private EliminarSolicitudNovedadCoordinadorMapper() {}

    public static EliminacionSolicitudNovedadCoordinadorDomain toDomain(
            EliminarSolicitudNovedadCoordinadorCommand command) {
        return EliminacionSolicitudNovedadCoordinadorDomain.crear(
                command.solicitud(), command.remitenteUsuario());
    }
}
