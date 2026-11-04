package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.entity.EvaluacionCuantitativaAsesorEntity;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.EvaluacionCuantitativaAsesorDomain;

public final class EvaluacionCuantitativaAsesorMapper {

    private EvaluacionCuantitativaAsesorMapper() {}

    public static EvaluacionCuantitativaAsesorEntity toEntity(EvaluacionCuantitativaAsesorDomain domain) {
        return new EvaluacionCuantitativaAsesorEntity(
                domain.getId(), domain.getEvaluacionAsesor(), domain.getItem(), domain.getPuntaje());
    }
}
