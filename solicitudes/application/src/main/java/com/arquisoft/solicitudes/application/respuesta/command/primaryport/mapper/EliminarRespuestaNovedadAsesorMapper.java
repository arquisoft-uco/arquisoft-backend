package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaNovedadAsesorCommand;
import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaNovedadAsesorDomain;

public final class EliminarRespuestaNovedadAsesorMapper {

    private EliminarRespuestaNovedadAsesorMapper() {}

    public static EliminacionRespuestaNovedadAsesorDomain toDomain(
            EliminarRespuestaNovedadAsesorCommand command) {
        return EliminacionRespuestaNovedadAsesorDomain.crear(
                command.solicitud(), command.asesorUsuario());
    }
}
