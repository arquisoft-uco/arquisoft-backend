package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudRegistroModificacionUsuariosCommand;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaDomain;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaSolicitudDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

public final class ResponderSolicitudRegistroModificacionUsuariosMapper {

    private ResponderSolicitudRegistroModificacionUsuariosMapper() {}

    public static RespuestaSolicitudDomain toDomain(
            ResponderSolicitudRegistroModificacionUsuariosCommand command) {
        var respuesta = RespuestaDomain.crear(command.solicitud(), command.contenido());
        return RespuestaSolicitudDomain.crear(
                respuesta, command.administradorUsuario(), TipoSolicitud.REGISTRO_Y_MODIFICACION_DE_USUARIOS);
    }
}
