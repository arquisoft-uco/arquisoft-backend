package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.domain.respuesta.ModificacionEstadoRespuestaDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

public final class ModificarEstadoRespuestaNovedadCoordinadorMapper {

    private ModificarEstadoRespuestaNovedadCoordinadorMapper() {}

    public static ModificacionEstadoRespuestaDomain toDomain(
            ModificarEstadoRespuestaNovedadCoordinadorCommand command) {
        return ModificacionEstadoRespuestaDomain.crear(
                command.solicitud(), command.coordinadorUsuario(), command.nuevoEstado(),
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);
    }
}
