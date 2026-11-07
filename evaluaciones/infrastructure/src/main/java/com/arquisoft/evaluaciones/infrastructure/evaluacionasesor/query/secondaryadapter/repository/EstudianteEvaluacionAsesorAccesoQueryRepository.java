package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface EstudianteEvaluacionAsesorAccesoQueryRepository
        extends QueryRepository<EstudianteEvaluacionAsesorAccesoJpaQueryEntity, UUID> {

    List<EstudianteEvaluacionAsesorAccesoJpaQueryEntity> findByEvaluacionAsesorId(UUID evaluacionAsesorId);
}
