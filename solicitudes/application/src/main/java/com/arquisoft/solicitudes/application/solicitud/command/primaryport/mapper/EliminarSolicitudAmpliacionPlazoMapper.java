package com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EliminarSolicitudAmpliacionPlazoCommand;
import com.arquisoft.solicitudes.domain.solicitud.EliminacionSolicitudDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

public final class EliminarSolicitudAmpliacionPlazoMapper {

    private EliminarSolicitudAmpliacionPlazoMapper() {}

    public static EliminacionSolicitudDomain toDomain(
            EliminarSolicitudAmpliacionPlazoCommand command) {
        return EliminacionSolicitudDomain.crear(
                command.solicitud(), command.remitenteUsuario(), TipoSolicitud.AMPLIACION_DE_PLAZO);
    }
}
