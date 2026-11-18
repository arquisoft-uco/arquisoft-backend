package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.secondaryport.entity.EvaluacionCuantitativaAsesorAccesoEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.secondaryadapter.repository.EvaluacionCuantitativaAsesorAccesoJpaQueryEntity;

public final class EvaluacionCuantitativaAsesorAccesoQueryMapper {

    private EvaluacionCuantitativaAsesorAccesoQueryMapper() {}

    public static EvaluacionCuantitativaAsesorAccesoEntity toEntity(
            EvaluacionCuantitativaAsesorAccesoJpaQueryEntity entity) {
        return new EvaluacionCuantitativaAsesorAccesoEntity(
                entity.getId(), entity.getEvaluacionAsesorId(), entity.getAsesorId());
    }
}
