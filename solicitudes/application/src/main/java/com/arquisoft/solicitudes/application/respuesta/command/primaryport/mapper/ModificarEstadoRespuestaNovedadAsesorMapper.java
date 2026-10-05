package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaNovedadAsesorCommand;
import com.arquisoft.solicitudes.domain.respuesta.ModificacionEstadoRespuestaDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

public final class ModificarEstadoRespuestaNovedadAsesorMapper {

    private ModificarEstadoRespuestaNovedadAsesorMapper() {}

    public static ModificacionEstadoRespuestaDomain toDomain(
            ModificarEstadoRespuestaNovedadAsesorCommand command) {
        return ModificacionEstadoRespuestaDomain.crear(
                command.solicitud(), command.asesorUsuario(), command.nuevoEstado(),
                TipoSolicitud.NOVEDAD_PARA_EL_ASESOR);
    }
}
