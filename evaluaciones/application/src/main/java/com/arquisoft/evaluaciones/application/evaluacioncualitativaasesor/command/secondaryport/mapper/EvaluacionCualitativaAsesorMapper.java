package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.entity.EvaluacionCualitativaAsesorEntity;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.EvaluacionCualitativaAsesorDomain;

public final class EvaluacionCualitativaAsesorMapper {

    private EvaluacionCualitativaAsesorMapper() {}

    public static EvaluacionCualitativaAsesorEntity toEntity(EvaluacionCualitativaAsesorDomain domain) {
        return new EvaluacionCualitativaAsesorEntity(
                domain.getId(), domain.getEvaluacionAsesor(), domain.getItem(), domain.getCriterio());
    }
}
