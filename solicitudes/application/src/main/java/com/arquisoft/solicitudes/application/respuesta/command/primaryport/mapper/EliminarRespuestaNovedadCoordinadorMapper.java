package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

public final class EliminarRespuestaNovedadCoordinadorMapper {

    private EliminarRespuestaNovedadCoordinadorMapper() {}

    public static EliminacionRespuestaDomain toDomain(
            EliminarRespuestaNovedadCoordinadorCommand command) {
        return EliminacionRespuestaDomain.crear(
                command.solicitud(), command.coordinadorUsuario(), TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);
    }
}
