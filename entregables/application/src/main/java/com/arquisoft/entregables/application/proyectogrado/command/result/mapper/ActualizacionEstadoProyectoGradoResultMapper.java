package com.arquisoft.entregables.application.proyectogrado.command.result.mapper;

import com.arquisoft.entregables.application.proyectogrado.command.result.ActualizacionEstadoProyectoGradoResult;
import com.arquisoft.entregables.domain.proyectogrado.ActualizacionEstadoProyectoGradoDomain;

import java.time.Instant;

public final class ActualizacionEstadoProyectoGradoResultMapper {

    private ActualizacionEstadoProyectoGradoResultMapper() {}

    public static ActualizacionEstadoProyectoGradoResult.Actualizado toResultActualizado(
            ActualizacionEstadoProyectoGradoDomain actualizacion) {
        return new ActualizacionEstadoProyectoGradoResult.Actualizado(
                actualizacion.getProyectoGrado(), actualizacion.getEstadoProyecto().getId());
    }

    public static ActualizacionEstadoProyectoGradoResult.Descartado toResultDescartado(
            ActualizacionEstadoProyectoGradoDomain actualizacion, Instant ocurridoEnGuardado) {
        return new ActualizacionEstadoProyectoGradoResult.Descartado(
                actualizacion.getProyectoGrado(), ocurridoEnGuardado);
    }
}
