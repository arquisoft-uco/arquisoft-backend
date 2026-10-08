package com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EstadoEvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.EstadoEvaluacionJuradoDomain;

public final class EstadoEvaluacionJuradoMapper {

    private EstadoEvaluacionJuradoMapper() {}

    public static EstadoEvaluacionJuradoDomain toDomain(EstadoEvaluacionJuradoEntity entity) {
        return EstadoEvaluacionJuradoDomain.reconstruir(
                entity.id(), entity.jurado(), EstadoEvaluacion.desde(entity.estado()));
    }
}
