package com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model.RegistrarResultadoEvaluacionEntregableProyectoGradoCommand;
import com.arquisoft.entregables.domain.entregableproyectogrado.ResultadoEvaluacionEntregableProyectoGradoDomain;

public final class RegistrarResultadoEvaluacionEntregableProyectoGradoMapper {

    private RegistrarResultadoEvaluacionEntregableProyectoGradoMapper() {}

    public static ResultadoEvaluacionEntregableProyectoGradoDomain toDomain(
            RegistrarResultadoEvaluacionEntregableProyectoGradoCommand command) {
        return ResultadoEvaluacionEntregableProyectoGradoDomain.crear(
                command.entregableProyectoGrado(), command.estadoEntregable());
    }
}
