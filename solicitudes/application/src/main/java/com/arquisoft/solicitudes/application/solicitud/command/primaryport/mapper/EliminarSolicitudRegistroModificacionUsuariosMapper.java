package com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EliminarSolicitudRegistroModificacionUsuariosCommand;
import com.arquisoft.solicitudes.domain.solicitud.EliminacionSolicitudDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

public final class EliminarSolicitudRegistroModificacionUsuariosMapper {

    private EliminarSolicitudRegistroModificacionUsuariosMapper() {}

    public static EliminacionSolicitudDomain toDomain(
            EliminarSolicitudRegistroModificacionUsuariosCommand command) {
        return EliminacionSolicitudDomain.crear(
                command.solicitud(), command.remitenteUsuario(), TipoSolicitud.REGISTRO_Y_MODIFICACION_DE_USUARIOS);
    }
}
