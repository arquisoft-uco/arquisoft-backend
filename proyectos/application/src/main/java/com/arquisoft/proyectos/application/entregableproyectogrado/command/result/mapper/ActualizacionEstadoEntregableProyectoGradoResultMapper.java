package com.arquisoft.proyectos.application.entregableproyectogrado.command.result.mapper;

import com.arquisoft.proyectos.application.entregableproyectogrado.command.result.ActualizacionEstadoEntregableProyectoGradoResult;
import com.arquisoft.proyectos.domain.entregableproyectogrado.EntregableProyectoGradoDomain;

import java.time.Instant;

public final class ActualizacionEstadoEntregableProyectoGradoResultMapper {

    private ActualizacionEstadoEntregableProyectoGradoResultMapper() {}

    public static ActualizacionEstadoEntregableProyectoGradoResult.Actualizado toResultActualizado(
            EntregableProyectoGradoDomain entregable) {
        return new ActualizacionEstadoEntregableProyectoGradoResult.Actualizado(
                entregable.getId(), entregable.getEstadoEntregable().getId());
    }

    public static ActualizacionEstadoEntregableProyectoGradoResult.Descartado toResultDescartado(
            EntregableProyectoGradoDomain entregable, Instant ocurridoEnVigente) {
        return new ActualizacionEstadoEntregableProyectoGradoResult.Descartado(
                entregable.getId(), ocurridoEnVigente);
    }
}
