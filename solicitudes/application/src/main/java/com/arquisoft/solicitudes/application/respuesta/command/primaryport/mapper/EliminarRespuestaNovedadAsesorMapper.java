package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaNovedadAsesorCommand;
import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

public final class EliminarRespuestaNovedadAsesorMapper {

    private EliminarRespuestaNovedadAsesorMapper() {}

    public static EliminacionRespuestaDomain toDomain(
            EliminarRespuestaNovedadAsesorCommand command) {
        return EliminacionRespuestaDomain.crear(
                command.solicitud(), command.asesorUsuario(), TipoSolicitud.NOVEDAD_PARA_EL_ASESOR);
    }
}
