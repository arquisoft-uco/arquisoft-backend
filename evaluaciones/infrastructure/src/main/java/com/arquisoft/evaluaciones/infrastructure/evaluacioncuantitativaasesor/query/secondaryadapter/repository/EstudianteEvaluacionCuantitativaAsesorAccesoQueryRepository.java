package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface EstudianteEvaluacionCuantitativaAsesorAccesoQueryRepository
        extends QueryRepository<EstudianteEvaluacionCuantitativaAsesorAccesoJpaQueryEntity, UUID> {

    List<EstudianteEvaluacionCuantitativaAsesorAccesoJpaQueryEntity> findByEvaluacionCuantitativaAsesorId(
            UUID evaluacionCuantitativaAsesorId);
}
