package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaRegistroModificacionUsuariosCommand;
import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

public final class EliminarRespuestaRegistroModificacionUsuariosMapper {

    private EliminarRespuestaRegistroModificacionUsuariosMapper() {}

    public static EliminacionRespuestaDomain toDomain(
            EliminarRespuestaRegistroModificacionUsuariosCommand command) {
        return EliminacionRespuestaDomain.crear(
                command.solicitud(), command.administradorUsuario(), TipoSolicitud.REGISTRO_Y_MODIFICACION_DE_USUARIOS);
    }
}
