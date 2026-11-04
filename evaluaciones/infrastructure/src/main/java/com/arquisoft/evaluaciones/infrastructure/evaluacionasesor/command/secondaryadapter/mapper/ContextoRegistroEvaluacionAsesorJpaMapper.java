package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.entity.ContextoRegistroEvaluacionAsesorEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.projection.ContextoRegistroEvaluacionAsesorProjection;

public final class ContextoRegistroEvaluacionAsesorJpaMapper {

    private ContextoRegistroEvaluacionAsesorJpaMapper() {}

    public static ContextoRegistroEvaluacionAsesorEntity toEntity(
            ContextoRegistroEvaluacionAsesorProjection proyeccion) {
        return new ContextoRegistroEvaluacionAsesorEntity(
                proyeccion.getId(), proyeccion.getEvaluacion(), proyeccion.getEstado(),
                proyeccion.getEntregable(), proyeccion.getAsesor());
    }
}
