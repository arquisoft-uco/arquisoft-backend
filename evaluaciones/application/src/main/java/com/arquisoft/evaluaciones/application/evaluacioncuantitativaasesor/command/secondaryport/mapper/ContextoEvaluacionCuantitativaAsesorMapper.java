package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.entity.ContextoEvaluacionCuantitativaAsesorEntity;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.ContextoEvaluacionCuantitativaAsesorDomain;

public final class ContextoEvaluacionCuantitativaAsesorMapper {

    private ContextoEvaluacionCuantitativaAsesorMapper() {}

    public static ContextoEvaluacionCuantitativaAsesorDomain toDomain(
            ContextoEvaluacionCuantitativaAsesorEntity entity) {
        return ContextoEvaluacionCuantitativaAsesorDomain.reconstruir(
                entity.id(), entity.evaluacionAsesor(), entity.asesor(), EstadoEvaluacion.desde(entity.estado()));
    }
}
