package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.domain.respuesta.ModificacionEstadoRespuestaNovedadCoordinadorDomain;

public final class ModificarEstadoRespuestaNovedadCoordinadorMapper {

    private ModificarEstadoRespuestaNovedadCoordinadorMapper() {}

    public static ModificacionEstadoRespuestaNovedadCoordinadorDomain toDomain(
            ModificarEstadoRespuestaNovedadCoordinadorCommand command) {
        return ModificacionEstadoRespuestaNovedadCoordinadorDomain.crear(
                command.solicitud(), command.coordinadorUsuario(), command.nuevoEstado());
    }
}
