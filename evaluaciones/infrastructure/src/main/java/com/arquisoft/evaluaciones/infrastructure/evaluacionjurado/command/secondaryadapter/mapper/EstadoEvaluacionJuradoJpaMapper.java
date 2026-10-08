package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EstadoEvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.projection.EstadoEvaluacionJuradoProjection;

public final class EstadoEvaluacionJuradoJpaMapper {

    private EstadoEvaluacionJuradoJpaMapper() {}

    public static EstadoEvaluacionJuradoEntity toEntity(EstadoEvaluacionJuradoProjection proyeccion) {
        return new EstadoEvaluacionJuradoEntity(
                proyeccion.getId(), proyeccion.getJurado(), proyeccion.getEstado());
    }
}
