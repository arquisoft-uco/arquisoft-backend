package com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.entity.EvaluacionAsesorEntity;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.EvaluacionAsesorDomain;

public final class EvaluacionAsesorMapper {

    private EvaluacionAsesorMapper() {}

    public static EvaluacionAsesorEntity toEntity(EvaluacionAsesorDomain domain) {
        return new EvaluacionAsesorEntity(domain.getId(), domain.getEvaluacion(), domain.getAsesor());
    }

    public static EvaluacionAsesorDomain toDomain(EvaluacionAsesorEntity entity) {
        return EvaluacionAsesorDomain.reconstruir(entity.id(), entity.evaluacion(), entity.asesor());
    }
}
