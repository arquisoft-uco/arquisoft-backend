package com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EliminarSolicitudCambioAsesorCommand;
import com.arquisoft.solicitudes.domain.solicitud.EliminacionSolicitudDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

public final class EliminarSolicitudCambioAsesorMapper {

    private EliminarSolicitudCambioAsesorMapper() {}

    public static EliminacionSolicitudDomain toDomain(
            EliminarSolicitudCambioAsesorCommand command) {
        return EliminacionSolicitudDomain.crear(
                command.solicitud(), command.remitenteUsuario(), TipoSolicitud.CAMBIO_DE_ASESOR);
    }
}
