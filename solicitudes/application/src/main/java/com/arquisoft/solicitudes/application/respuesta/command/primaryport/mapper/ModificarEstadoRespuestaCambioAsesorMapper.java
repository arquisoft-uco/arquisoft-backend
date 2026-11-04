package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaCambioAsesorCommand;
import com.arquisoft.solicitudes.domain.respuesta.ModificacionEstadoRespuestaDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

public final class ModificarEstadoRespuestaCambioAsesorMapper {

    private ModificarEstadoRespuestaCambioAsesorMapper() {}

    public static ModificacionEstadoRespuestaDomain toDomain(
            ModificarEstadoRespuestaCambioAsesorCommand command) {
        return ModificacionEstadoRespuestaDomain.crear(
                command.solicitud(), command.coordinadorUsuario(), command.nuevoEstado(),
                TipoSolicitud.CAMBIO_DE_ASESOR);
    }
}
