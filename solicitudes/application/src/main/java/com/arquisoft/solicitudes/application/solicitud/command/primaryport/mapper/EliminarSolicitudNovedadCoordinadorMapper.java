package com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EliminarSolicitudNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.domain.solicitud.EliminacionSolicitudDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

public final class EliminarSolicitudNovedadCoordinadorMapper {

    private EliminarSolicitudNovedadCoordinadorMapper() {}

    public static EliminacionSolicitudDomain toDomain(
            EliminarSolicitudNovedadCoordinadorCommand command) {
        return EliminacionSolicitudDomain.crear(
                command.solicitud(), command.remitenteUsuario(), TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);
    }
}
