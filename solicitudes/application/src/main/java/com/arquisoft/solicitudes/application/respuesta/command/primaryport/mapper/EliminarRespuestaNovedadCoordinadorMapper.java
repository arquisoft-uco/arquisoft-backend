package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaNovedadCoordinadorDomain;

public final class EliminarRespuestaNovedadCoordinadorMapper {

    private EliminarRespuestaNovedadCoordinadorMapper() {}

    public static EliminacionRespuestaNovedadCoordinadorDomain toDomain(
            EliminarRespuestaNovedadCoordinadorCommand command) {
        return EliminacionRespuestaNovedadCoordinadorDomain.crear(
                command.solicitud(), command.coordinadorUsuario());
    }
}
