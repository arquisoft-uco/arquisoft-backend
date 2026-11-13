package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.secondaryport.entity.EvaluacionCuantitativaAsesorAccesoEntity;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.AccesoEvaluacionCuantitativaAsesorDomain;

public final class EvaluacionCuantitativaAsesorAccesoMapper {

    private EvaluacionCuantitativaAsesorAccesoMapper() {}

    public static AccesoEvaluacionCuantitativaAsesorDomain toDomain(EvaluacionCuantitativaAsesorAccesoEntity entity) {
        return AccesoEvaluacionCuantitativaAsesorDomain.reconstruir(entity.evaluacionAsesor(), entity.asesor());
    }
}
