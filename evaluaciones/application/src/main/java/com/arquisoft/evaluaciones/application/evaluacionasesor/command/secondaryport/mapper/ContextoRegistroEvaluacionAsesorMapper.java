package com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.entity.ContextoRegistroEvaluacionAsesorEntity;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.ContextoRegistroEvaluacionAsesorDomain;

public final class ContextoRegistroEvaluacionAsesorMapper {

    private ContextoRegistroEvaluacionAsesorMapper() {}

    public static ContextoRegistroEvaluacionAsesorDomain toDomain(ContextoRegistroEvaluacionAsesorEntity entity) {
        return ContextoRegistroEvaluacionAsesorDomain.reconstruir(
                entity.id(), entity.evaluacion(), EstadoEvaluacion.desde(entity.estado()),
                entity.entregable(), entity.asesor());
    }
}
