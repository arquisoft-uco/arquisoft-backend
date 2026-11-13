package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.entity.ContextoEvaluacionCuantitativaAsesorEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.secondaryadapter.projection.ContextoEvaluacionCuantitativaAsesorProjection;

public final class ContextoEvaluacionCuantitativaAsesorJpaMapper {

    private ContextoEvaluacionCuantitativaAsesorJpaMapper() {}

    public static ContextoEvaluacionCuantitativaAsesorEntity toEntity(
            ContextoEvaluacionCuantitativaAsesorProjection proyeccion) {
        return new ContextoEvaluacionCuantitativaAsesorEntity(
                proyeccion.getId(), proyeccion.getEvaluacionAsesor(), proyeccion.getAsesor(),
                proyeccion.getEstado());
    }
}
