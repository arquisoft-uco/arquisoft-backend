package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaRegistroModificacionUsuariosCommand;
import com.arquisoft.solicitudes.domain.respuesta.ModificacionEstadoRespuestaDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

public final class ModificarEstadoRespuestaRegistroModificacionUsuariosMapper {

    private ModificarEstadoRespuestaRegistroModificacionUsuariosMapper() {}

    public static ModificacionEstadoRespuestaDomain toDomain(
            ModificarEstadoRespuestaRegistroModificacionUsuariosCommand command) {
        return ModificacionEstadoRespuestaDomain.crear(
                command.solicitud(), command.administradorUsuario(), command.nuevoEstado(),
                TipoSolicitud.REGISTRO_Y_MODIFICACION_DE_USUARIOS);
    }
}
