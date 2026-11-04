package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaCambioAsesorCommand;
import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

public final class EliminarRespuestaCambioAsesorMapper {

    private EliminarRespuestaCambioAsesorMapper() {}

    public static EliminacionRespuestaDomain toDomain(
            EliminarRespuestaCambioAsesorCommand command) {
        return EliminacionRespuestaDomain.crear(
                command.solicitud(), command.coordinadorUsuario(), TipoSolicitud.CAMBIO_DE_ASESOR);
    }
}
