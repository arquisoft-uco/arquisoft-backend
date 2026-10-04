package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.ContextoRegistroEvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.projection.ContextoRegistroEvaluacionJuradoProjection;

public final class ContextoRegistroEvaluacionJuradoJpaMapper {

    private ContextoRegistroEvaluacionJuradoJpaMapper() {}

    public static ContextoRegistroEvaluacionJuradoEntity toEntity(
            ContextoRegistroEvaluacionJuradoProjection proyeccion) {
        return new ContextoRegistroEvaluacionJuradoEntity(
                proyeccion.getId(), proyeccion.getEvaluacion(), proyeccion.getEstado(), proyeccion.getEntregable());
    }
}
