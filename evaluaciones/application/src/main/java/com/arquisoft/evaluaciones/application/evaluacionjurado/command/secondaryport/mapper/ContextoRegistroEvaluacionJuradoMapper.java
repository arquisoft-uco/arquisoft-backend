package com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.ContextoRegistroEvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.ContextoRegistroEvaluacionJuradoDomain;

public final class ContextoRegistroEvaluacionJuradoMapper {

    private ContextoRegistroEvaluacionJuradoMapper() {}

    public static ContextoRegistroEvaluacionJuradoDomain toDomain(ContextoRegistroEvaluacionJuradoEntity entity) {
        return ContextoRegistroEvaluacionJuradoDomain.reconstruir(
                entity.id(), entity.evaluacion(), EstadoEvaluacion.desde(entity.estado()), entity.entregable());
    }
}
